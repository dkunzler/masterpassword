package de.devland.masterpassword.ui.preferences;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.ui.LoginActivity;
import lombok.Setter;

/**
 * Created by David Kunzler on 11.09.2014.
 */
public class LanguagePreference extends ListPreference {

    protected String oldLanguage;
    protected DefaultPrefs defaultPrefs;
    @Setter
    protected AppCompatActivity settingsActivity;

    public LanguagePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LanguagePreference(Context context) {
        super(context);
        init();
    }

    void init() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        oldLanguage = defaultPrefs.language();
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
        this.setOnPreferenceChangeListener(null);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!oldLanguage.equals(defaultPrefs.language())) {
            RestartDialog restartDialog = new RestartDialog();
            restartDialog.show(settingsActivity.getSupportFragmentManager(), null);
        }
    }


    @SuppressLint("ValidFragment")
    public static class RestartDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(R.string.title_restartApp));
            builder.setMessage(getActivity().getString(R.string.msg_restartApp));
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // http://stackoverflow.com/questions/6609414/howto-programatically-restart-android-app
                    Intent mStartActivity = new Intent(getContext(), LoginActivity.class);
                    mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            return builder.create();
        }
    }
}
