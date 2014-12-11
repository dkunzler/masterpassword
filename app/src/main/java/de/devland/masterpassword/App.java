package de.devland.masterpassword;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import java.util.ArrayList;
import java.util.Locale;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.InputStickPrefs;
import de.devland.masterpassword.shared.BaseApp;
import de.devland.masterpassword.util.ProKeyUtil;
import de.devland.masterpassword.util.UpgradeManager;
import lombok.SneakyThrows;


/**
 * Created by David Kunzler on 23.08.2014.
 */
public class App extends BaseApp {

    private Locale targetLocale;


    @Override
    @SneakyThrows(PackageManager.NameNotFoundException.class)
    public void onCreate() {
        super.onCreate();
        ProKeyUtil.INSTANCE.initLicenseCheck();
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, this);
        InputStickPrefs inputStickPrefs = Esperandro.getPreferences(InputStickPrefs.class, this);
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        if (pInfo.versionCode != defaultPrefs.versionCode()) {
            UpgradeManager upgradeManager = new UpgradeManager(this);
            upgradeManager.onUpgrade(defaultPrefs.versionCode(), pInfo.versionCode);
        }
        defaultPrefs.versionName(pInfo.versionName);
        defaultPrefs.versionCode(pInfo.versionCode);
        defaultPrefs.initDefaults();
        inputStickPrefs.initDefaults();
        if (defaultPrefs.categories() == null) {
            defaultPrefs.categories(new ArrayList<Category>());
        }
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
}
