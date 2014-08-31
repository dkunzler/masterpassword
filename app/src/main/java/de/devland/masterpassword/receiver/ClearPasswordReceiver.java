package de.devland.masterpassword.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.MasterPasswordHolder;

/**
 * Created by David Kunzler on 28/08/14.
 */
public class ClearPasswordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MasterPasswordHolder.INSTANCE.clear();
    }
}
