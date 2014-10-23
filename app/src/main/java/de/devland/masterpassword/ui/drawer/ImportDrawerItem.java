package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ipaulpro.afilechooser.utils.FileUtils;

import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 06.10.2014.
 */
public class ImportDrawerItem extends SettingsDrawerItem {
    public static final int REQUEST_CODE_IMPORT = 1234;

    private Activity activity;

    public ImportDrawerItem(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getImageRes() {
        return R.drawable.ic_drawer_import;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_import;
    }

    @Override
    public void onClick(Context context) {
        Intent getContentIntent = FileUtils.createGetContentIntent();
        getContentIntent.setType("text/plain");

        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
        activity.startActivityForResult(intent, REQUEST_CODE_IMPORT);
    }

    public void doImport(Intent data) {
        // TODO
    }
}
