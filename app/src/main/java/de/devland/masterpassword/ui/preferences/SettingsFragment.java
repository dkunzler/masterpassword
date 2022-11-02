package de.devland.masterpassword.ui.preferences;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import androidx.annotation.NonNull;

import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.util.Utils;
import de.devland.masterpassword.util.event.ReloadDrawerEvent;

/**
 * Created by David Kunzler on 19.10.2014.
 */
public class SettingsFragment extends BaseSettingsFragment {

    public interface RequestPermissionsResultListener {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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
        ((UnlockFingerprintPreference) findPreference("fingerprintEnabled")).setSettingsActivity(getAppCompatActivity());
        ((InputStickPreference) findPreference("inputstick")).setSettingsActivity(getAppCompatActivity());
        ((PasswordAgePreference) findPreference("passwordage")).setSettingsActivity(getAppCompatActivity());
        bindPreferenceSummaryToValue(findPreference("language"));
        bindPreferenceSummaryToValue(findPreference("clipboardDuration"));
        bindPreferenceSummaryToValue(findPreference("autoLogoutDuration"));
        bindPreferenceSummaryToValue(findPreference("versionString"));
        bindPreferenceSummaryToValue(findPreference("defaultPasswordType"));
        Preference themeModePreference = findPreference("defaultThemeMode");
        findPreference("lockCategories").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                App.get().getBus().post(new ReloadDrawerEvent());
                return true;
            }
        });
        bindPreferenceSummaryToValue(themeModePreference);
        themeModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // bind to value
                bindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
                // set new theme value
                Utils.setThemeModeFromName(newValue.toString());
                return true;
            }
        });
    }


}
