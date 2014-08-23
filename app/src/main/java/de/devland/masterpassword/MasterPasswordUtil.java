package de.devland.masterpassword;

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

    public void clear() {
        masterPassword = null;
    }

}
