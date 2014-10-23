package de.devland.masterpassword.export;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.util.RequestCodeManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by David Kunzler on 23.10.2014.
 */
public class Exporter implements RequestCodeManager.RequestCodeCallback {
    public static final int REQUEST_CODE_EXPORT = 2345;
    public static final String EXTRA_EXPORT_TYPE = "de.devland.export.Exporter.EXPORT_TYPE";

    private Activity activity;

    public void startExportIntent(Activity activity, ExportType type) {
        this.activity = activity;
        // TODO pre 19
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String fileName = dateFormat.format(now) + "_export." + type.getFileExtension();


        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        Bundle extraData = new Bundle();
        extraData.putSerializable(EXTRA_EXPORT_TYPE, type);

        int requestCode = RequestCodeManager.INSTANCE.addRequest(REQUEST_CODE_EXPORT, this.getClass(), this, extraData);

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        String exportData = null;
        switch ((ExportType) data.getSerializable(EXTRA_EXPORT_TYPE)) {
            case MPSITES:
                // TODO
                // TODO not implemented message
                break;
            case JSON:
                List<Site> sites = Site.listAll(Site.class);
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                        .serializeNulls().create();
                exportData = gson.toJson(sites);
                break;
            default:
                Crouton.showText(activity, R.string.error_generic, Style.ALERT);
                return;
        }

        try {
            if (exportData != null) {
                ParcelFileDescriptor pfd = activity.getContentResolver().
                        openFileDescriptor(intent.getData(), "w");

                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                fileOutputStream.write(exportData.getBytes());
                fileOutputStream.close();
                pfd.close();
                Crouton.showText(activity, R.string.msg_exportDone, Style.CONFIRM);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Crouton.showText(activity, R.string.error_generic, Style.ALERT);
        }
    }
}
