package de.devland.masterpassword.export;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.williammora.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.RequestCodeManager;
import de.devland.masterpassword.util.event.ReloadDrawerEvent;

/**
 * Created by deekay on 26/10/14.
 */
public class Importer implements RequestCodeManager.RequestCodeCallback {
    public static final int REQUEST_CODE_IMPORT = 12345;
    public static final String EXTRA_IMPORT_TYPE = "de.devland.export.Exporter.IMPORT_TYPE";

    private Activity activity;
    private DefaultPrefs defaultPrefs;

    public void startImportIntent(Activity activity, ImportType importType) {
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
        intent.setType("text/plain");
        return intent;
    }

    private Intent getLegacyFileChooserIntent() {
        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent
                .putStringArrayListExtra(FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS,
                        new ArrayList<>(Arrays.asList(".json", ".mpsites")));
        getContentIntent.setType("text/plain");
        Intent intent = Intent
                .createChooser(getContentIntent, activity.getString(R.string.caption_selectFile));
        return intent;
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        if (resultCode == Activity.RESULT_OK) {
            ImportType importType = (ImportType) data.getSerializable(EXTRA_IMPORT_TYPE);
            ExportType exportType = ExportType.JSON; // TODO from url or data

            String contents = null;

            try {
                InputStream inputStream = activity.getContentResolver()
                                                  .openInputStream(intent.getData());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder fileContent = new StringBuilder("");
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line);
                }
                reader.close();
                inputStream.close();

                contents = fileContent.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.with(activity)
                        .text(activity.getString(R.string.error_generic))
                        .textColor(Color.RED)
                        .show(activity);
            }

            if (contents != null) {
                List<Site> importedSites = new ArrayList<>();

                switch (exportType) {
                    case MPSITES:
                        // TODO
                        break;
                    case JSON:
                        List<Site> gsonSites;
                        Gson gson = new GsonBuilder().setPrettyPrinting()
                                                     .excludeFieldsWithoutExposeAnnotation()
                                                     .serializeNulls().create();
                        Type listType = new TypeToken<ArrayList<Site>>() {
                        }.getType();
                        gsonSites = gson.fromJson(contents, listType);
                        if (gsonSites != null) {
                            importedSites = gsonSites;
                        } else {
                            Snackbar.with(activity)
                                    .text(activity.getString(R.string.error_generic))
                                    .textColor(Color.RED)
                                    .show(activity);
                        }
                        break;
                    default:
                        Snackbar.with(activity)
                                .text(activity.getString(R.string.error_generic))
                                .textColor(Color.RED)
                                .show(activity);
                        return;
                }

                if (importType == ImportType.OVERRIDE) {
                    Site.deleteAll(Site.class);
                    defaultPrefs.categories(new ArrayList<Category>());
                }

                List<Category> categories = defaultPrefs.categories();

                for (Site site : importedSites) {
                    site.save();
                    if (site.getCategory() != null) {
                        Category category = new Category(site.getCategory());
                        if (!categories.contains(category)) {
                            categories.add(category);
                        }
                    }
                }
                defaultPrefs.categories(categories);
                App.get().getBus().post(new ReloadDrawerEvent());

                Snackbar.with(activity)
                        .text(activity.getString(R.string.msg_importDone))
                        .show(activity);

            }


        }
    }
}
