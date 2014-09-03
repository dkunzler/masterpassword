package de.devland.masterpassword.util;

import android.content.Intent;

import com.lyndir.masterpassword.MPElementType;
import com.lyndir.masterpassword.MasterKey;

import de.devland.masterpassword.App;
import de.devland.masterpassword.ui.LoginActivity;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public enum MasterPasswordHolder {
    INSTANCE;

    private MasterKey masterKey;

    public boolean needsLogin(boolean redirect) {
        if (masterKey == null) {
            if (redirect) {
                Intent loginIntent = new Intent(App.get(), LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                App.get().startActivity(loginIntent);
            }
            return true;
        } else {
            return false;
        }
    }

    public void setMasterKey(MasterKey masterKey) {
        this.masterKey = masterKey;
    }

    public void clear() {
        masterKey.invalidate();
        masterKey = null;
    }

    public String generatePassword(MPElementType passwordType, String siteName, int siteCounter) {
        String result = "";
        if (masterKey != null) {
            result = masterKey.encode(siteName.trim(), passwordType, siteCounter);
        } else {
            Intent loginIntent = new Intent(App.get(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            App.get().startActivity(loginIntent);
        }
        return result;
    }
}
