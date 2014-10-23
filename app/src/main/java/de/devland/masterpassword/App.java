package de.devland.masterpassword;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import com.orm.Database;
import com.orm.SugarApp;
import com.squareup.otto.Bus;

import java.lang.reflect.Field;
import java.util.Locale;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.ui.BaseActivity;
import de.devland.masterpassword.util.ProKeyUtil;
import de.devland.masterpassword.util.UpgradeManager;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends SugarApp {

    private static App instance;
    private static Database db;

    private DefaultPrefs defaultPrefs;
    private Locale targetLocale;
    @Getter
    private Bus bus;
    @Getter
    @Setter
    private BaseActivity currentForegroundActivity;


    @Override
    @SneakyThrows(PackageManager.NameNotFoundException.class)
    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new Bus();
        ProKeyUtil.INSTANCE.initLicenseCheck();
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        if (pInfo.versionCode != defaultPrefs.versionCode()) {
            UpgradeManager upgradeManager = new UpgradeManager(this);
            upgradeManager.onUpgrade(defaultPrefs.versionCode(), pInfo.versionCode);
        }
        defaultPrefs.versionName(pInfo.versionName);
        defaultPrefs.versionCode(pInfo.versionCode);
        defaultPrefs.initDefaults();
        if (defaultPrefs.firstStart()) {
            defaultPrefs.firstStart(false);
        }

        // http://stackoverflow.com/questions/2264874/changing-locale-within-the-app-itself
        Configuration config = getBaseContext().getResources().getConfiguration();
        String lang = defaultPrefs.language();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            targetLocale = new Locale(lang);
            Locale.setDefault(targetLocale);
            config = new Configuration(config);
            config.locale = targetLocale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    // http://stackoverflow.com/questions/2264874/changing-locale-within-the-app-itself
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (targetLocale != null) {
            newConfig = new Configuration(newConfig);
            newConfig.locale = targetLocale;
            Locale.setDefault(targetLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
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
