package de.devland.masterpassword.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ClearClipboardService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getApplicationContext());
        Handler handler = new Handler();
        final ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);

        // TODO handler injection
        int clipboardDuration = Integer.parseInt(defaultPrefs.clipboardDuration());
        if (clipboardDuration > 0) {
            Crouton.makeText(App.get().getCurrentForegroundActivity(),
                    String.format(getApplicationContext().getString(
                                    R.string.copiedToClipboardWithDuration),
                            clipboardDuration), Style.INFO).show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClipData clip = ClipData.newPlainText("", "");
                    clipboard.setPrimaryClip(clip);
                    stopSelf();
                }
            }, clipboardDuration * 1000);
        } else {
            Crouton.makeText(App.get().getCurrentForegroundActivity(), R.string.copiedToClipboard, Style.INFO).show();
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
