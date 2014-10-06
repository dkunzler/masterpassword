package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by David Kunzler on 06.10.2014.
 */
public class ImportDrawerItem extends SettingsDrawerItem {
    private Activity activity;

    public ImportDrawerItem(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getImageRes() {
        return 0;
    }

    @Override
    public int getHeaderRes() {
        return 0;
    }

    @Override
    public void onClick(Context context) {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("text/plain");
        activity.startActivityForResult(intent, 1);
    }
}
