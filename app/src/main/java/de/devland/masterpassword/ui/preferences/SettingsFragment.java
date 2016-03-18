package de.devland.masterpassword.ui.preferences;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.support.v7.app.AppCompatDelegate;

import com.ipaulpro.afilechooser.FileChooserActivity;

import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.shared.util.Utils;

/**
 * Created by David Kunzler on 19.10.2014.
 */
public class SettingsFragment extends BaseSettingsFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

        // Add 'security' preferences
        addPreferencesFromResource(R.xml.pref_security);

        // Add 'about' preferences
        addPreferencesFromResource(R.xml.pref_about);

        ((LanguagePreference) findPreference("language")).setSettingsActivity(getAppCompatActivity());
        ((VerifyPasswordPreference) findPreference("verifyPassword")).setSettingsActivity(getAppCompatActivity());
        ((InputStickPreference) findPreference("inputstick")).setSettingsActivity(getAppCompatActivity());
        bindPreferenceSummaryToValue(findPreference("language"));
        bindPreferenceSummaryToValue(findPreference("clipboardDuration"));
        bindPreferenceSummaryToValue(findPreference("autoLogoutDuration"));
        bindPreferenceSummaryToValue(findPreference("versionString"));
        bindPreferenceSummaryToValue(findPreference("defaultPasswordType"));
//        Preference themeModePreference = findPreference("defaultThemeMode");
//        bindPreferenceSummaryToValue(themeModePreference);
//        themeModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                // bind to value
//                bindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
//                // set new theme value
//                Utils.setThemeModeFromName(newValue.toString());
//                return true;
//            }
//        });
        Preference useLegacyFileManager = findPreference("useLegacyFileManager");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            PreferenceCategory categoryGeneral = (PreferenceCategory) findPreference("category_general");
            categoryGeneral.removePreference(useLegacyFileManager);
        }
    }


}
