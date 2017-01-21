package de.devland.masterpassword.ui.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;

import de.devland.masterpassword.R;
import lombok.Setter;

/**
 * Created by deekay on 21.01.2017.
 */

public class PasswordAgePreference extends Preference {
    @Setter
    protected AppCompatActivity settingsActivity;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordAgePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PasswordAgePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PasswordAgePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordAgePreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        super.onClick();
        settingsActivity.getFragmentManager().
                beginTransaction().
                replace(R.id.container, new PasswordAgeSettingsFragment()).
                addToBackStack("passwordage").
                commit();
    }
}
