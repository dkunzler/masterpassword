package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.SettingsActivity;

/**
 * Created by David Kunzler on 06.09.2014.
 */
public class PreferencesDrawerItem extends SettingsDrawerItem {
    @Override
    public int getImageRes() {
        return R.drawable.ic_action_settings;
    }

    @Override
    public int getHeaderRes() {
        return R.string.title_activity_settings;
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
