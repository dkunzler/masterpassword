package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ipaulpro.afilechooser.utils.FileUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.devland.masterpassword.R;

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

    public void doExport(Uri fileUri) {
        // TODO
        String path = FileUtils.getPath(activity, fileUri);

    }
}
