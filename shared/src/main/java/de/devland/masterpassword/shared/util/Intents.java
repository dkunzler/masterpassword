package de.devland.masterpassword.shared.util;

/**
 * Created by David Kunzler on 25.11.2014.
 */
public interface Intents {

    public static final String EXTRA_PASSWORD = "de.devland.masterpassword.EXTRA_PASSWORD";
    public static final String EXTRA_LAYOUT = "de.devland.masterpassword.EXTRA_INPUTSTICK_LAYOUT";
    public static final String EXTRA_MESSAGE = "de.devland.masterpassword.EXTRA_MESSAGE";
    public static final String EXTRA_LICENSE = "de.devland.masterpassword.EXTRA_LICENSE";

    public static final String ACTION_INITLICENSECHECK = "de.devland.masterpassword.initiatelicensecheck";
    public static final String ACTION_ANSERLICENSECHECK = "de.devland.masterpassword.answerlicensecheck";
    public static final String ACTION_SENDMESSAGE = "de.devland.masterpassword.sendmessage";
    public static final String ACTION_SENDTOINPUTSTICK = "de.devland.masterpassword.sendtoinputstick";
}
