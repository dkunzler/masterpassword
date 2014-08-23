package de.devland.masterpassword;

import android.content.Intent;

import de.devland.masterpassword.ui.LoginActivity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by David Kunzler on 23.08.2014.
 */
public enum MasterPasswordUtil {
    INSTANCE;

    @Getter
    @Setter
    private String masterPassword;

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

    public void clear() {
        masterPassword = null;
    }

}
