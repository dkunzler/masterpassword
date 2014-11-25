package de.devland.masterpassword.pro.inputstick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inputstick.api.layout.UnitedStatesLayout;

import de.devland.masterpassword.shared.util.Intents;

/**
 * Created by David Kunzler on 24.11.2014.
 */
public class SendToInputStickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent activity = new Intent(context, SendToInputStickActivity.class);
        activity.putExtra(Intents.EXTRA_PASSWORD, intent.getExtras().getString(Intents.EXTRA_PASSWORD, ""));
        activity.putExtra(Intents.EXTRA_LAYOUT, intent.getExtras().getString(Intents.EXTRA_LAYOUT, UnitedStatesLayout.LOCALE_NAME));
        activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(activity);
    }
}
