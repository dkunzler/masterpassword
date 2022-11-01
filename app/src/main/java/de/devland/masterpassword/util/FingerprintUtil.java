package de.devland.masterpassword.util;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.ui.BaseActivity;
import de.devland.masterpassword.base.util.SnackbarUtil;
import de.devland.masterpassword.prefs.DefaultPrefs;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by David Kunzler on 16.04.2017.
 */
public class FingerprintUtil {

    private static final String TAG = "FingerprintUtil";

    private static final String KEY_NAME = "de.devland.masterpassword.key";

    public static boolean canUseFingerprint(boolean doSnackbar) {
        BaseActivity activity = App.get().getCurrentForegroundActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            KeyguardManager keyguardManager =
                    (KeyguardManager) App.get().getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) App.get().getSystemService(Context.FINGERPRINT_SERVICE);


            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, R.string.fingerprint_device_unsupported);
                return false;
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, R.string.fingerprint_permission);
                return false;
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, R.string.fingerprint_unconfigured);
                return false;
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                if (doSnackbar)
                    SnackbarUtil.showShort(activity, R.string.fingerprint_noLock);
                return false;
            }

            return true;
        } else {
            if (doSnackbar)
                SnackbarUtil.showShort(activity, R.string.fingerprint_device_unsupported);
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Cipher initEncryptCipher() throws FingerprintException {
        Cipher encryptCipher = getCipher(Cipher.ENCRYPT_MODE, getKeyStore(), null);
        if (encryptCipher == null) {
            // try again after recreating the keystore
            createKey();
            encryptCipher = getCipher(Cipher.ENCRYPT_MODE, getKeyStore(), null);
        }
        return encryptCipher;

    }

    private static KeyStore getKeyStore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Cipher initDecryptCipher(String iv) throws FingerprintException {
        return  getCipher(Cipher.DECRYPT_MODE, getKeyStore(), iv);
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    public static Pair<String, String> tryEncrypt(Cipher encryptCipher, String password, String login) {
        try {

            String secret = Base64.encodeToString(password.getBytes(), Base64.DEFAULT) + ":"
                    + Base64.encodeToString(login.getBytes(), Base64.DEFAULT);
            byte[] encrypted = encryptCipher.doFinal(secret.getBytes());

            IvParameterSpec ivParams = encryptCipher.getParameters().getParameterSpec(IvParameterSpec.class);
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            return new Pair<>(Base64.encodeToString(encrypted, Base64.DEFAULT), iv);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            SnackbarUtil.showLong(App.get().getCurrentForegroundActivity(), "Failed to encrypt the data with the generated key.");
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tries to decrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    public static Pair<String, String> tryDecrypt(Cipher decryptCipher, String payload) {
        try {

            byte[] encodedData = Base64.decode(payload, Base64.DEFAULT);
            byte[] decodedData = decryptCipher.doFinal(encodedData);
            String[] parts = new String(decodedData).split(":");
            return new Pair<>(new String(Base64.decode(parts[0], Base64.DEFAULT)),
                    new String(Base64.decode(parts[1], Base64.DEFAULT)));

        } catch (BadPaddingException | IllegalBlockSizeException | ArrayIndexOutOfBoundsException e) {
            SnackbarUtil.showLong(App.get().getCurrentForegroundActivity(), "Failed to decrypt the data with the generated key.");
            Log.e(TAG, "Failed to decrypt the data with the generated key." + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static SecretKey getKey(KeyStore keyStore) throws FingerprintException {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            if (key != null) return key;
            return createKey();
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | UnrecoverableKeyException e) {
            throw new FingerprintException(App.get().getString(R.string.fingeprint_secretKeyUnrecoverable), e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static SecretKey createKey() throws FingerprintException {
        try {

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            return keyGenerator.generateKey();

        } catch (Exception e) {
            throw new FingerprintException(App.get().getString(R.string.fingeprint_secretKeyGenerationFailed), e);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static Cipher getCipher(int mode, KeyStore keyStore, String ivString) throws FingerprintException {
        Cipher cipher;

        try {
            keyStore.load(null);
            byte[] iv;
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            IvParameterSpec ivParams;
            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, getKey(keyStore));
            } else {
                SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
                iv = Base64.decode(ivString, Base64.DEFAULT);
                ivParams = new IvParameterSpec(iv);
                cipher.init(mode, key, ivParams);
            }
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableKeyException | InvalidKeyException | KeyStoreException | InvalidAlgorithmParameterException | IOException | CertificateException e) {
            throw new FingerprintException(App.get().getString(R.string.fingeprint_cipherUnrecoverable), e);
        }
    }

    public static void resetFingerprintSettings() {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, App.get());
        defaultPrefs.fingerprintEnabled(false);
        defaultPrefs.encrypted(null);
        defaultPrefs.encryptionIV(null);

        try {
            KeyStore keyStore = getKeyStore();
            keyStore.load(null);
            keyStore.deleteEntry(KEY_NAME);
        } catch (KeyStoreException  | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "error deleting key", e);
        }
    }
}
