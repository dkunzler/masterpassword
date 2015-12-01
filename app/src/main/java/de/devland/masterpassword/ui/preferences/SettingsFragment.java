package de.devland.masterpassword.ui.preferences;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;

import de.devland.masterpassword.R;

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            PreferenceCategory categoryGeneral = (PreferenceCategory) findPreference("category_general");
            categoryGeneral.removePreference(findPreference("useLegacyFileManager"));
        }
    }


}
