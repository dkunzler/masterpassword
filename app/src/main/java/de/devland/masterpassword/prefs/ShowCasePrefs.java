package de.devland.masterpassword.prefs;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.SharedPreferenceMode;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by David Kunzler on 31/08/14.
 */
@SharedPreferences(mode = SharedPreferenceMode.PRIVATE, name = "ShowCasePrefs")
public interface ShowCasePrefs extends SharedPreferenceActions {

    @Default(ofBoolean = false)
    boolean loginShown();
    void loginShown(boolean loginShown);

    @Default(ofBoolean = false)
    boolean addCardShown();
    void addCardShown(boolean addCardShown);
}
