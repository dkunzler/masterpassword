package de.devland.masterpassword.ui.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;

/**
 * Created by deekay on 21.01.2017.
 */

public class EnablePasswordAgePreference extends CheckBoxPreference implements Preference.OnPreferenceChangeListener {

    protected DefaultPrefs defaultPrefs;
    protected BaseSettingsFragment settingsFragment;

    public EnablePasswordAgePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EnablePasswordAgePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public EnablePasswordAgePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EnablePasswordAgePreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        setOnPreferenceChangeListener(this);
    }

    public void setSettingsFragment(BaseSettingsFragment settingsFragment) {
        this.settingsFragment = settingsFragment;
        updatePreferenceEnabledStatus(isChecked());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        updatePreferenceEnabledStatus((Boolean) newValue);
        return true;
    }

    private void updatePreferenceEnabledStatus(boolean enabled) {
        settingsFragment.findPreference("passwordAgeModerate").setEnabled(enabled);
        settingsFragment.findPreference("passwordAgeCritical").setEnabled(enabled);
    }
}
