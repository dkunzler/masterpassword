package de.devland.masterpassword.util;

import android.content.Intent;

import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.MPSiteVariant;
import com.lyndir.masterpassword.MasterKey;

import java.util.HashMap;
import java.util.Map;

import de.devland.masterpassword.App;
import de.devland.masterpassword.ui.LoginActivity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public enum MasterPasswordHolder {
    INSTANCE;

    private boolean needsLogin = true;

    @Setter
    @Getter
    private String fullName;

    private Map<MasterKey.Version, MasterKey> masterkeys = new HashMap<>();


    public boolean needsLogin(boolean redirect) {
        if (needsLogin && redirect) {
            Intent loginIntent = new Intent(App.get(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            App.get().startActivity(loginIntent);
        }
        return needsLogin;
    }

    public void setMasterKey(MasterKey.Version version, MasterKey masterKey) {
        masterkeys.put(version, masterKey);
        needsLogin = false;
    }

    public void clear() {
        needsLogin = true;
        masterkeys.clear();
    }

    public String generatePassword(MPSiteType passwordType, MPSiteVariant variant, String siteName, int siteCounter, MasterKey.Version version) {
        String result = "";
        MasterKey masterKey = masterkeys.get(version);
        if (masterKey != null) {
            result = masterKey.encode(siteName.trim(), passwordType, siteCounter, variant, null);
        } else {
            needsLogin = true;
            Intent loginIntent = new Intent(App.get(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            App.get().startActivity(loginIntent);
        }
        return result;
    }
}
