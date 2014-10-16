package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;

import de.devland.masterpassword.R;
import de.devland.masterpassword.ui.preferences.SettingsActivity;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 06.09.2014.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class PreferencesDrawerItem extends SettingsDrawerItem {

    private final DrawerLayout drawerLayout;

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
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}
