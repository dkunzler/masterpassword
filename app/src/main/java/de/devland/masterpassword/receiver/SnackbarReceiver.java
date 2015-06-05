package de.devland.masterpassword.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import de.devland.masterpassword.App;
import de.devland.masterpassword.shared.ui.BaseActivity;
import de.devland.masterpassword.shared.util.Intents;

public class SnackbarReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(Intents.EXTRA_MESSAGE)) {
            String message = intent.getStringExtra(Intents.EXTRA_MESSAGE);
            BaseActivity currentForegroundActivity = App.get().getCurrentForegroundActivity();
            if (currentForegroundActivity != null) {
                Snackbar.make(currentForegroundActivity.findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
