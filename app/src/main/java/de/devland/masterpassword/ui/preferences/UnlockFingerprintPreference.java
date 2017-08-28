package de.devland.masterpassword.ui.preferences;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;

import com.squareup.otto.Subscribe;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.FingerprintUtil;
import lombok.Setter;

/**
 * Created by David Kunzler on 16.10.2014.
 */
public class UnlockFingerprintPreference extends CheckBoxPreference implements Preference.OnPreferenceChangeListener {

    protected DefaultPrefs defaultPrefs;
    @Setter
    protected AppCompatActivity settingsActivity;

    public UnlockFingerprintPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public UnlockFingerprintPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnlockFingerprintPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        setOnPreferenceChangeListener(this);
        App.get().getBus().register(this);
    }

    @Subscribe
    public void onCancel(UnlockFingerprintCancelEvent event) {
        setChecked(false);
    }


    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ((Boolean) newValue) {
            if (!FingerprintUtil.canUseFingerprint(true)) {
                this.setChecked(false);
                FingerprintUtil.resetFingerprintSettings();
            } else {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(settingsActivity, new String[]{Manifest.permission.USE_FINGERPRINT}, 42);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    this.setChecked(false);
                    FingerprintUtil.resetFingerprintSettings();
                } else {
                    UnlockFingerprintDialog verifyPasswordDialog = new UnlockFingerprintDialog();
                    verifyPasswordDialog.show(settingsActivity.getSupportFragmentManager(), null);
                }
            }
        } else {
            FingerprintUtil.resetFingerprintSettings();
        }
        return true;
    }

    public static class UnlockFingerprintCancelEvent {
    }

}
