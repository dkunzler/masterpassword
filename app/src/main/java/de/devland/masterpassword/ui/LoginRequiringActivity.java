package de.devland.masterpassword.ui;

import android.app.Activity;

import de.devland.masterpassword.MasterPasswordUtil;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public class LoginRequiringActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        if (MasterPasswordUtil.INSTANCE.needsLogin(true)) {
            this.finish();
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
