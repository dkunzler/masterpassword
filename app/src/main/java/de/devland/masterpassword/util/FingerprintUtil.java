package de.devland.masterpassword.util;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
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

import de.devland.masterpassword.App;
import de.devland.masterpassword.base.ui.BaseActivity;
import de.devland.masterpassword.base.util.SnackbarUtil;

import static android.content.ContentValues.TAG;
import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by deekay on 16.04.2017.
 */

public class FingerprintUtil {

    private static final String KEY_NAME = "de.devland.masterpassword.key";

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Cipher initEncryptCipher() {
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
    public static Cipher initDecryptCipher(String iv) {
        return  getCipher(Cipher.DECRYPT_MODE, getKeyStore(), iv);
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    public static Pair<String, String> tryEncrypt(Cipher encryptCipher, String secret) {
        try {

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
    public static String tryDecrypt(Cipher decryptCipher, String payload) {
        try {

            byte[] encodedData = Base64.decode(payload, Base64.DEFAULT);
            byte[] decodedData = decryptCipher.doFinal(encodedData);
            return new String(decodedData);

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            SnackbarUtil.showLong(App.get().getCurrentForegroundActivity(), "Failed to decrypt the data with the generated key.");
            Log.e(TAG, "Failed to decrypt the data with the generated key." + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static SecretKey getKey(KeyStore keyStore) {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            if (key != null) return key;
            return createKey();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static SecretKey createKey() {
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

        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static Cipher getCipher(int mode, KeyStore keyStore, String ivString) {
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
