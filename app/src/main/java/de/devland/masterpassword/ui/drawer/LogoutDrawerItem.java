package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.R;
import de.devland.masterpassword.ui.LoginActivity;
import de.devland.masterpassword.util.MasterPasswordHolder;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 04/09/14.
 */
@RequiredArgsConstructor
public class LogoutDrawerItem extends SettingsDrawerItem {

    public final Activity activity;

    @Override
    public int getImageRes() {
        return R.drawable.ic_drawer_logout;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_logout;
    }

    @Override
    public void onClick(Context context) {
        activity.finish();
        MasterPasswordHolder.INSTANCE.clear();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
