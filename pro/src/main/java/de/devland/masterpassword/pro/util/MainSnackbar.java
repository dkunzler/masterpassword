package de.devland.masterpassword.pro.util;

import android.content.Context;
import android.content.Intent;

import de.devland.masterpassword.shared.util.Intents;

/**
 * Created by David Kunzler on 25.11.2014.
 */
public class MainSnackbar {
    public static void send(Context context, String message) {
        Intent broadcast = new Intent();
        broadcast.setAction("de.devland.masterpassword.sendmessage");
        broadcast.putExtra(Intents.EXTRA_MESSAGE, message);
        context.sendBroadcast(broadcast);
    }
}
