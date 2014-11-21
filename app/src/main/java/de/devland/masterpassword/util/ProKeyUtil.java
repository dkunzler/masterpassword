package de.devland.masterpassword.util;

import android.content.Intent;

import de.devland.masterpassword.App;
import de.devland.masterpassword.shared.BaseApp;

/**
 * Created by David Kunzler on 31/08/14.
 */
public enum ProKeyUtil {
    INSTANCE;

    protected boolean isPro = false;

    public void setPro(boolean isPro) {
        this.isPro = isPro;
        // TODO send event to update UI
    }

    public boolean isPro() {
        return true;
    }

    public void initLicenseCheck() {
        BaseApp app = App.get();
        Intent broadcast = new Intent();
        broadcast.setAction("de.devland.masterpassword.initiatelicensecheck");
        app.sendBroadcast(broadcast);
    }
}
