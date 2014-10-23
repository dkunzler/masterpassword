package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.export.ExportType;

/**
 * Created by David Kunzler on 06.10.2014.
 */
public class ExportDrawerItem extends SettingsDrawerItem {
    public static final int REQUEST_CODE_EXPORT = 2345;

    private Activity activity;

    public ExportDrawerItem(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getImageRes() {
        return R.drawable.ic_drawer_export;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_export;
    }

    @Override
    public void onClick(Context context) {
        // TODO pre 19
        // TODO dialog to choose file format
        Date now = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        String fileName = dateFormat.format(now) + "_export.mpsites";


        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        activity.startActivityForResult(intent, REQUEST_CODE_EXPORT);
    }

    public void doExport(Intent data) {
        // TODO
        File exportFile = new File(data.getData().getPath());
        String exportData = null;
        switch (ExportType.fromUri(data.getData())) {
            case MPSITES:
                // TODO
                break;
            case JSON:
                List<Site> sites = Site.listAll(Site.class);
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                             .serializeNulls().create();
                exportData = gson.toJson(sites);
                break;
            default:
                // TODO error message
                return;
        }

        OutputStreamWriter outputStreamWriter;
        try {
            if (exportData != null) {
                outputStreamWriter = new OutputStreamWriter(new FileOutputStream(exportFile));
                outputStreamWriter.write(exportData);
                outputStreamWriter.close();
                // TODO done message
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO error message
        }

    }
}
