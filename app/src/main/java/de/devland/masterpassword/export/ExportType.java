package de.devland.masterpassword.export;

import android.net.Uri;

/**
 * Created by deekay on 22/10/14.
 */
public enum ExportType {
    JSON, MPSITES;

    public static ExportType fromUri(Uri uri) {
        String uriPath = uri.getPath();
        if (uriPath.endsWith(".mpsites")) {
            return MPSITES;
        } else if (uriPath.endsWith(".json")) {
            return JSON;
        } else {
            return null;
        }
    }
}
