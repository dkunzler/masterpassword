package de.devland.masterpassword.ui.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.ProKeyUtil;

/**
 * Created by David Kunzler on 26.11.2014.
 */
public class EnableInputStickPreference extends CheckBoxPreference implements Preference.OnPreferenceChangeListener {

    protected DefaultPrefs defaultPrefs;
    protected BaseSettingsFragment settingsFragment;

    public EnableInputStickPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EnableInputStickPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public EnableInputStickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EnableInputStickPreference(Context context) {
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
        if ((Boolean) newValue) {
            if (!ProKeyUtil.INSTANCE.isPro()) {
                setChecked(false);
                ProKeyUtil.INSTANCE.showGoProDialog(settingsFragment.getAppCompatActivity());
                return false;
            } else {
                updatePreferenceEnabledStatus((Boolean) newValue);
                return true;
            }
        }
        updatePreferenceEnabledStatus((Boolean) newValue);
        return true;
    }

    private void updatePreferenceEnabledStatus(boolean enabled) {
        settingsFragment.findPreference("inputstickKeymap").setEnabled(enabled);
    }
}
