package de.devland.masterpassword.util;

import android.content.Context;

import com.lyndir.masterpassword.MPElementType;
import com.lyndir.masterpassword.entity.MPElementEntity;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;

/**
 * Created by deekay on 01/10/14.
 */
public class UpgradeManager {

    private Context context;

    public UpgradeManager(Context context) {
        this.context = context;
    }

    public void onUpgrade(int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 4:
                DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
                if (defaultPrefs.defaultPasswordType().equals("\"GeneratedMaximum\"")) {
                    defaultPrefs.defaultPasswordType(MPElementType.GeneratedMaximum.toString());
                }
                defaultPrefs.hidePasswords(defaultPrefs.hidePasswords());
        }
    }

}
