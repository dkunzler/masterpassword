package de.devland.masterpassword;

import android.app.Application;

import com.orm.Database;
import com.orm.SugarApp;

import java.lang.reflect.Field;


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

    public static Database getDb() {
        try {
            Field database = SugarApp.class.getDeclaredField("database");
            database.setAccessible(true);
            return (Database) database.get(App.get());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
