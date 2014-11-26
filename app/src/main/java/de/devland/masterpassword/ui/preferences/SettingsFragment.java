package de.devland.masterpassword.ui.preferences;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

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

        ((LanguagePreference) findPreference("language")).setSettingsActivity((ActionBarActivity) getActivity());
        ((VerifyPasswordPreference) findPreference("verifyPassword")).setSettingsActivity((ActionBarActivity) getActivity());
        ((InputStickPreference) findPreference("inputstick")).setSettingsActivity((ActionBarActivity) getActivity());
        bindPreferenceSummaryToValue(findPreference("language"));
        bindPreferenceSummaryToValue(findPreference("clipboardDuration"));
        bindPreferenceSummaryToValue(findPreference("autoLogoutDuration"));
        bindPreferenceSummaryToValue(findPreference("versionName"));
        bindPreferenceSummaryToValue(findPreference("defaultPasswordType"));
    }


}
