package de.devland.masterpassword;

import android.app.Application;

import com.orm.SugarApp;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends SugarApp {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App get() {
        return instance;
    }
}
