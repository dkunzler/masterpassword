package de.devland.masterpassword.prefs;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by David Kunzler on 23.10.2014.
 */
@SharedPreferences(name = "InternalPrefs")
public interface InternalPrefs {
    int requestCodeSeed();
    void requestCodeSeed(int seed);
}
