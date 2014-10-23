package de.devland.masterpassword.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.receiver.ClearPasswordReceiver;
import de.devland.masterpassword.util.MasterPasswordHolder;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public class LoginRequiringActivity extends BaseActivity {

    protected DefaultPrefs defaultPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        if (MasterPasswordHolder.INSTANCE.needsLogin(true)) {
            this.finish();
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!MasterPasswordHolder.INSTANCE.needsLogin(false)) {
            int autoLogoutDuration = Integer.parseInt(defaultPrefs.autoLogoutDuration());
            if (autoLogoutDuration > 0) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                long triggerTime = System.currentTimeMillis() + 1000 * 60 * autoLogoutDuration;
                Intent intent = new Intent(this, ClearPasswordReceiver.class);
                PendingIntent broadcast = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC, triggerTime, broadcast);
            }
        }
    }


}
