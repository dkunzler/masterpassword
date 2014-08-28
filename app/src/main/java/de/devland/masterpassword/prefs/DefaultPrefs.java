package de.devland.masterpassword.prefs;

import java.util.List;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;
import de.devland.masterpassword.model.Category;

/**
 * Created by David Kunzler on 28/08/14.
 */
@SharedPreferences
public interface DefaultPrefs extends SharedPreferenceActions {
    @Default
    String defaultUserName();
    void defaultUserName(String userName);

    List<Category> categories();
    void categories(List<Category> categories);
}
