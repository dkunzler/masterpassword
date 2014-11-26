package de.devland.masterpassword.ui.preferences;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 26.11.2014.
 */
public class InputStickSettingsFragment extends BaseSettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_inputstick);

        ((EnableInputStickPreference) findPreference("inputstickEnabled")).setSettingsActivity((ActionBarActivity) getActivity());
        bindPreferenceSummaryToValue(findPreference("inputstickKeymap"));
    }
}
