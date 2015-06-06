package de.devland.masterpassword.export;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.lyndir.masterpassword.model.MPSite;
import com.lyndir.masterpassword.model.MPSiteUnmarshaller;
import com.lyndir.masterpassword.model.MPUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.shared.util.RequestCodeManager;
import de.devland.masterpassword.shared.util.SnackbarUtil;
import de.devland.masterpassword.util.ProKeyUtil;
import de.devland.masterpassword.util.event.ReloadDrawerEvent;

/**
 * Created by deekay on 26/10/14.
 */
public class Importer implements RequestCodeManager.RequestCodeCallback {
    public static final int REQUEST_CODE_IMPORT = 12345;
    public static final String EXTRA_IMPORT_TYPE = "de.devland.export.Exporter.IMPORT_TYPE";

    private AppCompatActivity activity;
    private DefaultPrefs defaultPrefs;

    JsonDeserializer<Date> timeStampDeserializer = new JsonDeserializer<Date>() {
        private final DateFormat enUsFormat
                = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US);
        private final DateFormat localFormat
                = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT);
        private final DateFormat iso8601Format = buildIso8601Format();

        private DateFormat buildIso8601Format() {
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601Format;
        }

        private Date parseAsTimeStamp(JsonElement json) {
            return json == null ? null : new Date(json.getAsLong());
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
            try {
                return parseAsTimeStamp(json);
            } catch (Exception ignored) {
            }
            try {
                return localFormat.parse(json.getAsString());
            } catch (ParseException ignored) {
            }
            try {
                return enUsFormat.parse(json.getAsString());
            } catch (ParseException ignored) {
            }
            try {
                return iso8601Format.parse(json.getAsString());
            } catch (ParseException e) {
                throw new JsonSyntaxException(json.getAsString(), e);
            }
        }
    };

    public void startImportIntent(AppCompatActivity activity, ImportType importType) {
        this.activity = activity;
        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, activity);

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = getStorageAccessFrameworkIntent();
        } else {
            intent = getLegacyFileChooserIntent();
        }

        Bundle extraData = new Bundle();
        extraData.putSerializable(EXTRA_IMPORT_TYPE, importType);
        int requestCode = RequestCodeManager.INSTANCE
                .addRequest(REQUEST_CODE_IMPORT, this.getClass(), this, extraData);
        activity.startActivityForResult(intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Intent getStorageAccessFrameworkIntent() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("*/*");
        return intent;
    }

    private Intent getLegacyFileChooserIntent() {
        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent
                .putStringArrayListExtra(FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS,
                        new ArrayList<>(Arrays.asList(".json", ".mpsites")));
        getContentIntent.setType("text/plain");
        return Intent
                .createChooser(getContentIntent, activity.getString(R.string.caption_selectFile));
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        if (resultCode == Activity.RESULT_OK) {
            ImportType importType = (ImportType) data.getSerializable(EXTRA_IMPORT_TYPE);
            ExportType exportType = ExportType.JSON; // TODO from url or data

            List<String> lines = new ArrayList<>();

            try {
                InputStream inputStream = activity.getContentResolver()
                        .openInputStream(intent.getData());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("##")) {
                        // found header declaration
                        exportType = ExportType.MPSITES;
                    }
                    lines.add(line);
                }
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                SnackbarUtil.showShort(activity, R.string.error_generic);
                return;
            }

            if (lines.size() > 0) {
                List<Site> importedSites = new ArrayList<>();

                switch (exportType) {
                    case MPSITES:
                        if (!ProKeyUtil.INSTANCE.isPro()) {
                            ProKeyUtil.INSTANCE.showGoProDialog(activity);
                            return;
                        }
                        try {
                            MPSiteUnmarshaller unmarshaller = MPSiteUnmarshaller.unmarshall(lines);
                            MPUser user = unmarshaller.getUser();
                            defaultPrefs.defaultPasswordType(user.getDefaultType().toString());
                            defaultPrefs.defaultUserName(user.getFullName());
                            for (MPSite mpSite : user.getSites()) {
                                importedSites.add(Site.fromMPSite(mpSite));
                            }
                        } catch (Exception e) {
                            SnackbarUtil.showShort(activity, R.string.error_generic);
                            return;
                        }
                        break;
                    case JSON:
                        StringBuilder fileContent = new StringBuilder("");
                        for (String line : lines) {
                            fileContent.append(line);
                        }
                        List<Site> gsonSites;
                        Gson gson = new GsonBuilder().setPrettyPrinting()
                                .registerTypeAdapter(Date.class, timeStampDeserializer)
                                .excludeFieldsWithoutExposeAnnotation()
                                .serializeNulls().create();
                        Type listType = new TypeToken<ArrayList<Site>>() {
                        }.getType();
                        gsonSites = gson.fromJson(fileContent.toString(), listType);
                        if (gsonSites != null) {
                            importedSites = gsonSites;
                        } else {
                            SnackbarUtil.showShort(activity, R.string.error_generic);
                            return;
                        }
                        break;
                    default:
                        SnackbarUtil.showShort(activity, R.string.error_generic);
                        return;
                }

                if (importType == ImportType.OVERRIDE) {
                    Site.deleteAll(Site.class);
                    defaultPrefs.categories(new ArrayList<Category>());
                }

                List<Category> categories = defaultPrefs.categories();

                for (Site site : importedSites) {
                    site.save();
                    if (!Strings.isNullOrEmpty(site.getCategory())) {
                        Category category = new Category(site.getCategory());
                        if (!categories.contains(category)) {
                            categories.add(category);
                        }
                    }
                }
                defaultPrefs.categories(categories);
                App.get().getBus().post(new ReloadDrawerEvent());

                SnackbarUtil.showShort(activity, R.string.msg_importDone);
            }


        }
    }
}
