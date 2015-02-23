package de.devland.masterpassword.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.lyndir.masterpassword.MasterKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.devland.masterpassword.R;
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
        dialog = ProgressDialog.show(context, null, context.getString(R.string.msg_generateKey), true, false);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String name = strings[0];
        String password = strings[1];

        Set<MasterKey.Version> versions = new TreeSet<>();
        versions.add(MasterKey.Version.CURRENT);

        List<Site> sitesDistinctVersions = Site.find(Site.class, null, null, Site.ALGORITHM_VERSION, null, null);
        for (Site site : sitesDistinctVersions) {
            versions.add(site.getAlgorithmVersion());
        }

        List<Thread> threads = new ArrayList<>();

        for (MasterKey.Version version : versions) {
            Thread thread = createThread(name, password, version);
            thread.start();
            threads.add(thread);
        }

        while (!threads.isEmpty()) {
            // join until no threads left
            Thread thread = threads.get(0);
            try {
                thread.join();
                threads.remove(thread);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return true;
    }

    private Thread createThread(final String name, final String password, final MasterKey.Version version) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                MasterKey masterKey = MasterKey.create(version, name, password.toCharArray());
                MasterPasswordHolder.INSTANCE.setMasterKey(version, masterKey);
            }
        };
        return new Thread(runnable);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (dialog != null) {
            dialog.dismiss();
        }
        callback.run();
    }
}
