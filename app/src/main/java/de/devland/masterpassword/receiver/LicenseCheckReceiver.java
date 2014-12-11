package de.devland.masterpassword.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.shared.util.Intents;
import de.devland.masterpassword.util.ProKeyUtil;

public class LicenseCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(Intents.EXTRA_LICENSE)) {
            boolean hasLicense = intent.getBooleanExtra(Intents.EXTRA_LICENSE, false);
            ProKeyUtil.INSTANCE.setPro(hasLicense);
        }
    }
}
