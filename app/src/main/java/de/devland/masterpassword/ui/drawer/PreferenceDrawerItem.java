package de.devland.masterpassword.ui.drawer;

import android.content.Context;

import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 04/09/14.
 */
public class PreferenceDrawerItem extends SettingsDrawerItem {
    @Override
    public int getImageRes() {
        return 0;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_settings;
    }

    @Override
    public void onClick(Context context) {

    }
}
