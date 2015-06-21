package de.devland.masterpassword.export;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.model.MPSiteMarshaller;
import com.lyndir.masterpassword.model.MPUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.shared.util.RequestCodeManager;
import de.devland.masterpassword.shared.util.SnackbarUtil;
import de.devland.masterpassword.util.MasterPasswordHolder;

/**
 * Created by David Kunzler on 23.10.2014.
 */
public class Exporter implements RequestCodeManager.RequestCodeCallback {
    public static final int REQUEST_CODE_EXPORT = 2345;
    public static final String EXTRA_EXPORT_TYPE = "de.devland.export.Exporter.EXPORT_TYPE";
    public static final String EXTRA_FILE_NAME = "de.devland.export.Exporter.FILE_NAME";

    private Activity activity;

    JsonSerializer<Date> timeStampSerializer = new JsonSerializer<Date>() {
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                context) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    };

    public void startExportIntent(Activity activity, ExportType type) {
        this.activity = activity;
        Date now = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String fileName = dateFormat.format(now) + "_export." + type.getFileExtension();


        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = getStorageAccessFrameworkIntent(fileName);
        } else {
            intent = getLegacyFolderChooserIntent();
        }

        Bundle extraData = new Bundle();
        extraData.putSerializable(EXTRA_EXPORT_TYPE, type);
        extraData.putString(EXTRA_FILE_NAME, fileName);
        int requestCode = RequestCodeManager.INSTANCE
                .addRequest(REQUEST_CODE_EXPORT, this.getClass(), this, extraData);

        activity.startActivityForResult(intent, requestCode);
    }

    private Intent getLegacyFolderChooserIntent() {
        Intent getContentIntent = new Intent(activity, FileChooserActivity.class);
        // do not show any files by selecting an invalid file extension to filterText
        getContentIntent.putStringArrayListExtra(
                FileChooserActivity.EXTRA_FILTER_INCLUDE_EXTENSIONS,
                new ArrayList<>(Arrays.asList("./")));
        getContentIntent.putExtra(FileChooserActivity.EXTRA_SELECT_FOLDER, true);
        getContentIntent.setType("text/plain");
        return getContentIntent;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Intent getStorageAccessFrameworkIntent(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        return intent;
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        if (resultCode == Activity.RESULT_OK) {
            String exportData;
            String fileName = data.getString(EXTRA_FILE_NAME);
            List<Site> sites = Site.listAll(Site.class);
            switch ((ExportType) data.getSerializable(EXTRA_EXPORT_TYPE)) {
                case MPSITES:
                    DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, activity);
                    MPUser user = new MPUser(defaultPrefs.defaultUserName(), MasterPasswordHolder.INSTANCE.getKeyId());
                    user.setDefaultType(MPSiteType.valueOf(defaultPrefs.defaultPasswordType()));
                    for (Site site : sites) {
                        user.addSite(site.toMPSite(user));
                    }
                    exportData = MPSiteMarshaller.marshallSafe(user).getExport();
                    break;
                case JSON:
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .registerTypeAdapter(Date.class, timeStampSerializer)
                            .excludeFieldsWithoutExposeAnnotation()
                            .serializeNulls().create();
                    exportData = gson.toJson(sites);
                    break;
                default:
                    SnackbarUtil.showShort(activity, R.string.error_generic);
                    return;
            }

            try {
                if (exportData != null) {
                    FileOutputStream fileOutputStream;
                    ParcelFileDescriptor pfd = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        pfd = activity.getContentResolver().
                                openFileDescriptor(intent.getData(), "w");

                        fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                    } else {
                        String path = FileUtils.getPath(activity, intent.getData());
                        File file = new File(path, fileName);
                        fileOutputStream = new FileOutputStream(file);
                    }
                    fileOutputStream.write(exportData.getBytes());
                    fileOutputStream.close();
                    if (pfd != null) {
                        pfd.close();
                    }
                    SnackbarUtil.showShort(activity, R.string.msg_exportDone);
                }
            } catch (IOException e) {
                e.printStackTrace();
                SnackbarUtil.showShort(activity, R.string.error_generic);
            }
        }
    }
}
