package de.devland.masterpassword.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.util.ProKeyUtil;

public class LicenseCheckReceiver extends BroadcastReceiver {
    private static final String EXTRA_LICENSE = "de.devland.masterpassword.EXTRA_LICENSE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(EXTRA_LICENSE)) {
            boolean hasLicense = intent.getBooleanExtra(EXTRA_LICENSE, false);
            ProKeyUtil.INSTANCE.setPro(hasLicense);
        }
    }
}
