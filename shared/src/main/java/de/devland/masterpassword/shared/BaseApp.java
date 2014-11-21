package de.devland.masterpassword.shared;

import com.orm.SugarApp;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import de.devland.masterpassword.shared.ui.BaseActivity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by David Kunzler on 21.11.2014.
 */
public class BaseApp extends SugarApp {

    @Getter
    private static BaseApp instance;

    @Getter
    private Bus bus;
    @Getter
    @Setter
    private BaseActivity currentForegroundActivity;

    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new Bus(ThreadEnforcer.ANY);
    }

    public static BaseApp get() {
        return instance;
    }
}
