package de.devland.masterpassword.prefs;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by David Kunzler on 29.12.2014.
 */
@SharedPreferences(name = "ProPrefs")
public interface ProPrefs extends SharedPreferenceActions {

    @Default(ofBoolean = false)
    boolean lastRemoteStatus();
    void lastRemoteStatus(boolean lastRemoteStatus);

    @Default(ofLong = 0l)
    long lastRemoteCheck();
    void lastRemoteCheck(long lastRemoteCheck);
}
