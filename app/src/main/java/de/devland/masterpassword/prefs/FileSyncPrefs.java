package de.devland.masterpassword.prefs;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by deekay on 26.10.2017.
 */
@SharedPreferences
public interface FileSyncPrefs {
    void fileSyncActivated(boolean fileSyncActivated);
    boolean fileSyncActivated();

    void fileSyncUri(String fileSyncUri);
    String fileSyncUri();
}
