package de.devland.masterpassword.ui.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;

import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 09.10.2014.
 */
public class FeedbackPreference extends Preference {
    public FeedbackPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FeedbackPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedbackPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "info@devland.de", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Master Password Feedback");
        String mailTemplate = "\n\n\n-----\n" +
                "OS Version: %s\n" +
                "API Level: %d\n" +
                "Android Version: %s\n" +
                "Device Manufacturer: %s\n" +
                "Device Codename: %s\n" +
                "Device Model: %s";
        String mailText = String.format(mailTemplate,
                System.getProperty("os.version"),
                Build.VERSION.SDK_INT,
                Build.VERSION.RELEASE,
                Build.MANUFACTURER,
                Build.DEVICE,
                Build.MODEL);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mailText);
        getContext().startActivity(Intent.createChooser(emailIntent, getContext().getString(R.string.title_feedback)));
    }
}
