package de.devland.masterpassword.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.lyndir.masterpassword.MasterKey;

import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 25/08/14.
 */
public class GenerateUserKeysAsyncTask extends AsyncTask<String, Integer, MasterKey> {

    private Context context;
    private ProgressDialog dialog;
    private Runnable callback;

    public GenerateUserKeysAsyncTask(Context context, Runnable callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_generateKey), true, false);
    }

    @Override
    protected MasterKey doInBackground(String... strings) {
        return new MasterKey(strings[0].trim(), strings[1].trim());
    }

    @Override
    protected void onPostExecute(MasterKey masterKey) {
        super.onPostExecute(masterKey);
        MasterPasswordHolder.INSTANCE.setMasterKey(masterKey);
        if (dialog != null) {
            dialog.dismiss();
        }
        callback.run();
    }
}
