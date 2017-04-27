package de.devland.masterpassword.util;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import de.devland.masterpassword.App;
import de.devland.masterpassword.base.ui.BaseActivity;
import de.devland.masterpassword.base.util.SnackbarUtil;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by deekay on 16.04.2017.
 */

public class FingerprintUtil {

    public static boolean canUseFingerprint(boolean doSnackbar) {
        BaseActivity activity = App.get().getCurrentForegroundActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            KeyguardManager keyguardManager =
                    (KeyguardManager) App.get().getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) App.get().getSystemService(FINGERPRINT_SERVICE);


            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, "Your device does not support fingerprint unlock");
                return false;
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, "Please enable the fingerprint permission");
                return false;
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, "No fingerprint configured. Please register at least one fingerprint in your device's Settings");
                return false;
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, "Please enable lockscreen security in your device's Settings");
                return false;
            }

            return true;
        } else {
            if (doSnackbar)
                SnackbarUtil.showShort(activity, "Your device does not support fingerprint unlock");
            return false;
        }
    }
}
