package de.devland.masterpassword.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.lyndir.masterpassword.MasterKey;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;

/**
 * Created by David Kunzler on 25/08/14.
 */
public class GenerateUserKeysAsyncTask extends AsyncTask<String, Integer, Object> {

    private Context context;
    private ProgressDialog dialog;
    private Runnable callback;
    private DefaultPrefs defaultPrefs;

    public GenerateUserKeysAsyncTask(Context context, Runnable callback) {
        this.context = context;
        this.callback = callback;
        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_generateKey), true, false);
    }

    @Override
    protected Object doInBackground(String... strings) {
        if (defaultPrefs.legacyMode()) {
            return new com.lyndir.masterpassword.legacy.MasterKey(strings[0].trim(), strings[1].trim());
        } else {
            return new MasterKey(strings[0].trim(), strings[1].trim());
        }
    }

    @Override
    protected void onPostExecute(Object masterKey) {
        super.onPostExecute(masterKey);
        if (defaultPrefs.legacyMode()) {
            MasterPasswordHolder.INSTANCE.setLegacyMasterKey((com.lyndir.masterpassword.legacy.MasterKey) masterKey);
        } else {
            MasterPasswordHolder.INSTANCE.setMasterKey((MasterKey) masterKey);
        }

        if (dialog != null) {
            dialog.dismiss();
        }
        callback.run();
    }
}
