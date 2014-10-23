package de.devland.masterpassword.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.prefs.InternalPrefs;
import lombok.SneakyThrows;

/**
 * Created by David Kunzler on 23.10.2014.
 */
public enum RequestCodeManager {
    INSTANCE;

    protected Map<Integer, RequestCodeCallback> callbacks;
    protected Map<Integer, Bundle> callbackData;
    protected InternalPrefs internalPrefs;

    RequestCodeManager() {
        callbackData = new HashMap<>();
        callbacks = new HashMap<>();
        internalPrefs = Esperandro.getPreferences(InternalPrefs.class, App.get());
    }

    private int getBaseSeed() {
        int i = internalPrefs.requestCodeSeed();
        Random random = new Random();
        while (i == 0) {
            i = random.nextInt();
            internalPrefs.requestCodeSeed(i);
        }
        return i;
    }

    @SneakyThrows(PackageManager.NameNotFoundException.class)
    public int getRequestCode(Class clazz, int clientRequestCode) {
        int packageHash = App.get().getPackageManager().getApplicationInfo(App.get().getPackageName(), PackageManager.GET_META_DATA).uid;
        int seed = getBaseSeed();
        int classHash = clazz.getName().hashCode();
        short toShort = (short) (clientRequestCode + classHash * seed * packageHash);
        return Math.abs(toShort);
    }

    public int addRequest(int internalRequestCode, Class clazz, RequestCodeCallback callback, Bundle data) {
        int requestCode = getRequestCode(clazz, internalRequestCode);

        callbacks.put(requestCode, callback);
        callbackData.put(requestCode, data);

        return requestCode;
    }

    public boolean execute(int requestCode, int resultCode, Intent intent) {
        boolean handled = false;
        if (callbacks.containsKey(requestCode) && callbackData.containsKey(requestCode)) {
            callbacks.get(requestCode).run(resultCode, intent, callbackData.get(requestCode));
            handled = true;
        }
        callbacks.remove(requestCode);
        callbackData.remove(requestCode);

        return handled;
    }

    public static interface RequestCodeCallback {
        public void run(int resultCode, Intent intent, Bundle data);
    }
}
