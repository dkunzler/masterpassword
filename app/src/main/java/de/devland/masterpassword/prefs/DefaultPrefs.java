package de.devland.masterpassword.prefs;

import java.util.List;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.annotations.Default;
import de.devland.esperandro.annotations.SharedPreferences;
import de.devland.masterpassword.base.util.Utils;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;

/**
 * Created by David Kunzler on 28/08/14.
 */
@SharedPreferences
public interface DefaultPrefs extends SharedPreferenceActions {

    @Default(ofBoolean = true)
    boolean firstStart();
    void firstStart(boolean firstStart);

    @Default(ofBoolean = true)
    boolean saveUserName();
    void saveUserName(boolean saveUserName);

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

    String versionString();
    void versionString(String versionString);

    @Default(ofBoolean = false)
    boolean hidePasswords();
    void hidePasswords(boolean hidePasswords);

    int versionCode();
    void versionCode(int versionCode);

    @Default(ofString = "GeneratedMaximum:Password")
    String defaultPasswordType();
    void defaultPasswordType(String defaultPasswordType);

    @Default(ofString = "")
    String language();
    void language(String language);

    @Default(ofString = Site.SITE_NAME + Site.NOCASE_ORDER_SUFFIX)
    String sortBy();
    void sortBy(String sortBy);

    @Default(ofBoolean = true)
    boolean showCanary();
    void showCanary(boolean showCanary);

    @Default(ofBoolean = false)
    boolean useLegacyFileManager();
    void useLegacyFileManager(boolean useLegacyFileManager);

    @Default(ofString = Utils.ThemeMode.LIGHT)
    String defaultThemeMode();
    void defaultThemeMode(String defaultThemeMode);

}
