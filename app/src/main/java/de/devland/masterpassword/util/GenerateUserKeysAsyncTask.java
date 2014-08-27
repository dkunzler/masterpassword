package de.devland.masterpassword.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Iterator;
import java.util.List;

import de.devland.masterpassword.MasterPasswordUtil;
import de.devland.masterpassword.model.Site;

/**
 * Created by David Kunzler on 25/08/14.
 */
public class GenerateUserKeysAsyncTask extends AsyncTask<String, Integer, Boolean> {

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
        dialog = ProgressDialog.show(context, null, "Generating Passwords...", true, false);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        Iterator<Site> sites = Site.findAll(Site.class);
        Site site;
        while (sites.hasNext()) {
            site = sites.next();
            MasterPasswordUtil.INSTANCE.getKeyForUserName(site.getUserName());
        }
        return Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (dialog != null) {
            dialog.dismiss();
        }
        callback.run();
    }
}
