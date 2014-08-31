package de.devland.masterpassword.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.MasterPasswordHolder;
import de.devland.masterpassword.receiver.ClearPasswordReceiver;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public class LoginRequiringActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        if (MasterPasswordHolder.INSTANCE.needsLogin(true)) {
            this.finish();
            this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            long triggerTime = System.currentTimeMillis() + 1000 * 60 * 10; // 10 min
            Intent intent = new Intent(this, ClearPasswordReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, triggerTime, broadcast);
        }
    }
}
