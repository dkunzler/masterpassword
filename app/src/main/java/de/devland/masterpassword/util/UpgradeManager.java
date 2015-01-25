package de.devland.masterpassword.util;

import android.content.Context;

import com.google.common.base.Strings;
import com.lyndir.masterpassword.MPSiteType;

import java.util.ArrayList;
import java.util.List;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.ShowCasePrefs;

/**
 * Created by deekay on 01/10/14.
 */
public class UpgradeManager {

    private Context context;

    public UpgradeManager(Context context) {
        this.context = context;
    }

    public void onUpgrade(int oldVersion, int newVersion) {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        ShowCasePrefs showCasePrefs = Esperandro.getPreferences(ShowCasePrefs.class, context);
        if (defaultPrefs.defaultPasswordType().equals("\"GeneratedMaximum\"")) {
            defaultPrefs.defaultPasswordType(MPSiteType.GeneratedMaximum.toString());
        }
        if (defaultPrefs.sortBy().equals(Site.SITE_NAME)) {
            defaultPrefs.sortBy(Site.SITE_NAME + Site.NOCASE_ORDER_SUFFIX);
        }
        List<Category> categories = defaultPrefs.categories();
        List<Category> toRemove = new ArrayList<>();
        if (categories != null) {
            for (Category category : categories) {
                if (Strings.isNullOrEmpty(category.getName())) {
                    toRemove.add(category);
                }
            }
            categories.removeAll(toRemove);
        }

        if (oldVersion < 20) {
            showCasePrefs.legacyModeDialogShown(false);
        }
    }

}
