package de.devland.masterpassword.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.shared.BaseApp;
import de.devland.masterpassword.shared.util.Intents;
import de.devland.masterpassword.util.event.ProStatusChangeEvent;

/**
 * Created by David Kunzler on 31/08/14.
 */
public enum ProKeyUtil {
    INSTANCE;

    protected static final String PRO_PACKAGE_NAME = "de.devland.masterpassword.pro";

    protected boolean isPro = false;

    public void setPro(boolean isPro) {
        this.isPro = isPro;
        App.get().getBus().post(new ProStatusChangeEvent(isPro));
    }

    public boolean isPro() {
        return isPro;
    }

    public void initLicenseCheck() {
        boolean exists = true;
        PackageManager pm = App.get().getPackageManager();
        try {
            pm.getPackageInfo(PRO_PACKAGE_NAME, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            exists = false;
        }
        setPro(exists);
        BaseApp app = App.get();
        Intent broadcast = new Intent();
        broadcast.setAction(Intents.ACTION_INITLICENSECHECK);
        app.sendBroadcast(broadcast);
    }

    public void showGoProDialog(ActionBarActivity activity) {
        GoProDialog goProDialog = new GoProDialog(activity);
        goProDialog.show(activity.getSupportFragmentManager(), null);
    }

    @SuppressLint("ValidFragment")
    private class GoProDialog extends DialogFragment {
        private ActionBarActivity activity;

        public GoProDialog(ActionBarActivity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle(R.string.title_proFeature);
            builder.setMessage(R.string.msg_proFeature);
            builder.setCancelable(true);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(R.string.caption_playStore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PRO_PACKAGE_NAME)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + PRO_PACKAGE_NAME)));
                    }
                }
            });

            return builder.create();
        }
    }
}
