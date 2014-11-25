package de.devland.masterpassword.pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InitiateLicenseCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LicenseCheckService.class);
        context.startService(service);
    }
}
