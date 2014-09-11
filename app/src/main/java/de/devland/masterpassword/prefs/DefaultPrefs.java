package de.devland.masterpassword.prefs;

import com.lyndir.masterpassword.MPElementType;

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

    @Default(ofBoolean = true)
    boolean firstStart();
    void firstStart(boolean firstStart);

    @Default
    String defaultUserName();
    void defaultUserName(String userName);

    String masterPasswordHash();
    void masterPasswordHash(String masterPasswordHash);

    List<Category> categories();
    void categories(List<Category> categories);

    @Default(ofBoolean = false)
    boolean verifyPassword();
    void verifyPassword(boolean verifyPassword);

    @Default(ofString = "20")
    String clipboardDuration();
    void clipboardDuration(String clipboardDuration);

    @Default(ofString = "10")
    String autoLogoutDuration();
    void autoLogoutDuration(String autoLogoutDuration);

    String versionName();
    void versionName(String versionName);

    int versionCode();
    void versionCode(int versionCode);

    MPElementType defaultPasswordType();
    void defaultPasswordType(MPElementType defaultPasswordType);

    @Default(ofString = "")
    String language();
    void language(String language);
}
