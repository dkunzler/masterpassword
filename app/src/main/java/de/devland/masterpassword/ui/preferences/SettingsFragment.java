package de.devland.masterpassword.ui.preferences;

import android.os.Bundle;

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

        ((LanguagePreference) findPreference("language")).setSettingsActivity(getActionBarActivity());
        ((VerifyPasswordPreference) findPreference("verifyPassword")).setSettingsActivity(getActionBarActivity());
        ((InputStickPreference) findPreference("inputstick")).setSettingsActivity(getActionBarActivity());
        bindPreferenceSummaryToValue(findPreference("language"));
        bindPreferenceSummaryToValue(findPreference("clipboardDuration"));
        bindPreferenceSummaryToValue(findPreference("autoLogoutDuration"));
        bindPreferenceSummaryToValue(findPreference("versionString"));
        bindPreferenceSummaryToValue(findPreference("defaultPasswordType"));
    }


}
