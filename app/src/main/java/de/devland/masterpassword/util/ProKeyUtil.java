package de.devland.masterpassword.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import java.util.Date;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.BuildConfig;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.ProPrefs;
import de.devland.masterpassword.shared.BaseApp;
import de.devland.masterpassword.shared.util.Intents;
import de.devland.masterpassword.util.event.ProStatusChangeEvent;

/**
 * Created by David Kunzler on 31/08/14.
 */
public enum ProKeyUtil {
    INSTANCE;

    private static final long _24_HOURS = 1000 * 60 * 60 * 24;

    protected boolean isPro = false;

    public void setPro(boolean isPro, boolean remote) {
        this.isPro = isPro;
        App.get().getBus().post(new ProStatusChangeEvent(isPro));
        if (remote) {
            ProPrefs proPrefs = proPrefs();
            proPrefs.lastRemoteCheck((new Date()).getTime());
            proPrefs.lastRemoteStatus(isPro);
        }
    }

    public boolean isPro() {
        return isPro;
    }

    public void initLicenseCheck() {
        boolean exists = proExists();
        if (exists) {
            ProPrefs proPrefs = proPrefs();
            Date then = new Date(proPrefs.lastRemoteCheck());
            Date now = new Date();
            if (then.before(new Date(now.getTime() - _24_HOURS)) || !proPrefs.lastRemoteStatus()) {
                setPro(exists, false);
                BaseApp app = App.get();
                Intent broadcast = new Intent();
                broadcast.setAction(Intents.ACTION_INITLICENSECHECK);
                app.sendBroadcast(broadcast);
            } else {
                setPro(exists && proPrefs.lastRemoteStatus(), false);
            }
        }
    }

    private boolean proExists() {
        boolean exists = true;
        PackageManager pm = App.get().getPackageManager();
        try {
            if (BuildConfig.DEBUG) {
                pm.getPackageInfo(Intents.PACKAGE_NAME_PRO_DEBUG, PackageManager.GET_META_DATA);
            } else {
                pm.getPackageInfo(Intents.PACKAGE_NAME_PRO, PackageManager.GET_META_DATA);
            }
        } catch (PackageManager.NameNotFoundException e) {
            exists = false;
        }
        return exists;
    }

    public void showGoProDialog(ActionBarActivity activity) {
        if (proExists()) {
            ProVerificationProblemDialog proVerificationProblemDialog = new ProVerificationProblemDialog(activity);
            proVerificationProblemDialog.show(activity.getSupportFragmentManager(), null);
        } else {
            GoProDialog goProDialog = new GoProDialog(activity);
            goProDialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    private ProPrefs proPrefs() {
        return Esperandro.getPreferences(ProPrefs.class, App.get());
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
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Intents.PACKAGE_NAME_PRO)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + Intents.PACKAGE_NAME_PRO)));
                    }
                }
            });

            return builder.create();
        }
    }

    @SuppressLint("ValidFragment")
    private class ProVerificationProblemDialog extends DialogFragment {
        private ActionBarActivity activity;

        public ProVerificationProblemDialog(ActionBarActivity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle(R.string.title_proVerificationError);
            builder.setMessage(R.string.msg_proVerificationError);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.title_feedback, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, activity);

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "info@devland.de", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Master Password Feedback");
                    String mailTemplate = "\n\n\n-----\n" +
                            "App Version: %s\n" +
                            "App Version Code: %d\n" +
                            "OS Version: %s\n" +
                            "API Level: %d\n" +
                            "Android Version: %s\n" +
                            "Device Manufacturer: %s\n" +
                            "Device Codename: %s\n" +
                            "Device Model: %s";
                    String mailText = String.format(mailTemplate,
                            defaultPrefs.versionName(),
                            defaultPrefs.versionCode(),
                            System.getProperty("os.version"),
                            Build.VERSION.SDK_INT,
                            Build.VERSION.RELEASE,
                            Build.MANUFACTURER,
                            Build.DEVICE,
                            Build.MODEL);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, mailText);
                    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.title_feedback)));
                }
            });
            builder.setPositiveButton(android.R.string.ok, null);

            return builder.create();
        }
    }
}
