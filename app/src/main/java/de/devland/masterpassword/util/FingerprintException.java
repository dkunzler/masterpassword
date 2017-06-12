package de.devland.masterpassword.util;

import android.util.Log;

/**
 * Created by deekay on 12.06.2017.
 */

public class FingerprintException extends Exception {
    public FingerprintException(String message, Throwable cause) {
        super(message, cause);
        Log.e("Fingerprint", message, cause);
    }
}
