package de.devland.masterpassword.prefs;

import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by deekay on 18.03.2015.
 */
@SharedPreferences(name = "statePrefs")
public interface State {
    String currentCategory();
    void currentCategory(String currentCategory);
}
