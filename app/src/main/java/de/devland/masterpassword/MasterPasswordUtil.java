package de.devland.masterpassword;

import android.content.Intent;

import com.lyndir.lhunath.masterpassword.MasterPassword;

import java.util.HashMap;
import java.util.Map;

import de.devland.masterpassword.ui.LoginActivity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public enum MasterPasswordUtil {
    INSTANCE;

    private String masterPassword;

    private Map<String, byte[]> keysForUserName = new HashMap<String, byte[]>();

    public boolean needsLogin(boolean redirect) {
        if (masterPassword == null) {
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

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void clear() {
        masterPassword = null;
        keysForUserName.clear();
    }


    public byte[] getKeyForUserName(String userName) {
        byte[] keyForUser;

        if (!keysForUserName.containsKey(userName)) {
            keyForUser = MasterPassword.keyForPassword(masterPassword, userName); // TODO Background
        } else {
            keyForUser = keysForUserName.get(userName);
        }

        return keyForUser;
    }
}
