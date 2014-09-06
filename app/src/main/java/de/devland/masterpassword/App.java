package de.devland.masterpassword;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.orm.Database;
import com.orm.SugarApp;

import java.lang.reflect.Field;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;
import lombok.SneakyThrows;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends SugarApp {

    private static App instance;
    private static Database db;

    @Override
    @SneakyThrows(PackageManager.NameNotFoundException.class)
    public void onCreate() {
        super.onCreate();
        instance = this;
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        defaultPrefs.versionName(pInfo.versionName);
        defaultPrefs.versionCode(pInfo.versionCode);
        if (defaultPrefs.firstStart()) {
            defaultPrefs.initDefaults();
            defaultPrefs.firstStart(false);
        }
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
