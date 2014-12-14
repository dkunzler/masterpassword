package de.devland.masterpassword.shared;

import com.orm.SugarApp;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import de.devland.masterpassword.shared.ui.BaseActivity;

/**
 * Created by David Kunzler on 21.11.2014.
 */
public class BaseApp extends SugarApp {

    private static BaseApp instance;

    private Bus bus;
    private BaseActivity currentForegroundActivity;

    public static BaseApp getInstance() {
        return BaseApp.instance;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public static BaseApp get() {
        return instance;
    }

    public Bus getBus() {
        return this.bus;
    }

    public BaseActivity getCurrentForegroundActivity() {
        return this.currentForegroundActivity;
    }

    public void setCurrentForegroundActivity(BaseActivity currentForegroundActivity) {
        this.currentForegroundActivity = currentForegroundActivity;
    }
}
