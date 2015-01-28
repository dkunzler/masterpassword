package de.devland.masterpassword.util;

import android.content.Intent;

import com.lyndir.lhunath.opal.system.CodeUtils;
import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.MPSiteVariant;
import com.lyndir.masterpassword.MasterKey;
import com.lyndir.masterpassword.legacy.MPElementType;

import de.devland.masterpassword.App;
import de.devland.masterpassword.ui.LoginActivity;
import lombok.Getter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public enum MasterPasswordHolder {
    INSTANCE;

    private boolean needsLogin = true;

    @Getter
    private String fullName;
    @Getter
    private byte[] keyId;
    private MasterKey masterKey;
    private com.lyndir.masterpassword.legacy.MasterKey legacyMasterKey;

    public boolean needsLogin(boolean redirect) {
        if (needsLogin && redirect) {
            Intent loginIntent = new Intent(App.get(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            App.get().startActivity(loginIntent);
        }
        return needsLogin;
    }

    public void setMasterKey(MasterKey masterKey) {
        this.masterKey = masterKey;
        needsLogin = false;
        fullName = masterKey.getFullName();
        keyId = masterKey.getKeyID();
    }

    public void setLegacyMasterKey(com.lyndir.masterpassword.legacy.MasterKey masterKey) {
        this.legacyMasterKey = masterKey;
        needsLogin = false;
        fullName = legacyMasterKey.getUserName();
        keyId = CodeUtils.decodeHex(legacyMasterKey.getKeyID());
    }

    public void clear() {
        needsLogin = true;
        if (masterKey != null) {
            masterKey.invalidate();
            masterKey = null;
        }
        if (legacyMasterKey != null) {
            legacyMasterKey.invalidate();
            legacyMasterKey = null;
        }
    }

    public String generatePassword(MPSiteType passwordType, String siteName, int siteCounter, boolean legacy) {
        String result = "";
        if (legacy && legacyMasterKey != null) {
            result = legacyMasterKey.encode(siteName.trim(), MPElementType.valueOf(passwordType.name()), siteCounter);
        } else if (!legacy && masterKey != null) {
            result = masterKey.encode(siteName.trim(), passwordType, siteCounter, MPSiteVariant.Password, null);
        } else {
            needsLogin = true;
            Intent loginIntent = new Intent(App.get(), LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            App.get().startActivity(loginIntent);
        }
        return result;
    }
}
