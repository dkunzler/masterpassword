package de.devland.masterpassword.util;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionItemTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.ShowCasePrefs;

/**
 * Created by David Kunzler on 01.09.2014.
 */
public enum ShowCaseManager {
    INSTANCE;

    private ShowCasePrefs showCasePrefs;

    private ShowCaseManager() {
        showCasePrefs = Esperandro.getPreferences(ShowCasePrefs.class, App.get());
    }

    public void showEditShowCase(Activity activity, View target) {
        if (!showCasePrefs.editShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(activity, true);
            showCaseBuilder.hideOnTouchOutside()
                    .setContentTitle("A Site")
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("Site Name, Password Type and Site Counter are required to derive the password. User Name is a reminder when logging in to the site and therefore optional.")
                    .setTarget(new ViewTarget(target));
            showCaseBuilder.build().show();
            showCasePrefs.editShown(true);
        }
    }

    public void showLoginShowCase(Activity activity, View target) {
        if (!showCasePrefs.loginShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(activity, true);
            showCaseBuilder.hideOnTouchOutside()
                    .setContentTitle("Your Credentials")
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("The combination of your full name and a master password will be used to derive your different site passwords.")
                    .setTarget(new ViewTarget(target));
            showCaseBuilder.build().show();
            showCasePrefs.loginShown(true);
        }
    }

    public void showAddShowCase(Activity activity) {
        if (!showCasePrefs.addCardShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(activity, true);
            showCaseBuilder.hideOnTouchOutside()
                    .setContentTitle("Add Site")
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("Add a site you want to generate a password for.")
                    .setTarget(new ActionItemTarget(activity, R.id.action_add));
            showCaseBuilder.build().show();
            showCasePrefs.addCardShown(true);
        }
    }

    public void showFirstCardShowCase(Activity activity) {
        if (!showCasePrefs.firstCardShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(activity, true);
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            showCaseBuilder.hideOnTouchOutside()
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("Swipe to delete.\nTap password to copy to clipboard.")
                    .setTarget(new PointTarget(size.x / 2, size.y / 4));
            showCaseBuilder.build().show();
            showCasePrefs.firstCardShown(true);
        }
    }
}
