package de.devland.masterpassword;

import com.orm.Database;
import com.orm.SugarApp;

import java.lang.reflect.Field;

import lombok.SneakyThrows;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends SugarApp {

    private static App instance;
    private static Database db;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App get() {
        return instance;
    }

    @SneakyThrows({NoSuchFieldException.class, IllegalAccessException.class})
    public static Database getDb() {
        if (db == null) {
            Field database = SugarApp.class.getDeclaredField("database");
            database.setAccessible(true);
            db = (Database) database.get(App.get());
        }
        return db;
    }
}
