package de.devland.masterpassword.prefs;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by David Kunzler on 26.11.2014.
 */
@SharedPreferences
public interface InputStickPrefs extends SharedPreferenceActions {
    boolean inputstickEnabled();
    void inputstickEnabled(boolean enabled);

    @Default(ofString = "de-DE")
    String inputstickKeymap();
    void inputstickKeymap(String keymap);
}
