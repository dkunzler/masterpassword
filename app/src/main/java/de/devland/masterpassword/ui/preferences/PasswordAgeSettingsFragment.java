package de.devland.masterpassword.ui.preferences;

import android.os.Bundle;

import de.devland.masterpassword.R;

/**
 * Created by deekay on 21.01.2017.
 */

public class PasswordAgeSettingsFragment extends BaseSettingsFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_passwordage);

        ((EnablePasswordAgePreference) findPreference("visualizePasswordAge")).setSettingsFragment(this);
        bindPreferenceSummaryToValue(findPreference("passwordAgeModerate"));
        bindPreferenceSummaryToValue(findPreference("passwordAgeCritical"));
    }
}
