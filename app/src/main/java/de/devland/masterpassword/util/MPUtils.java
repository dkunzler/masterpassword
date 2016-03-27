package de.devland.masterpassword.util;

import android.util.Pair;

import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.MPSiteVariant;

/**
 * Created by deekay on 27.03.2016.
 */
public class MPUtils {

    public static Pair<MPSiteType, MPSiteVariant> extractMPSiteParameters(String passwordTypeValue) {
        String[] typeAndVariant = passwordTypeValue.split(":");
        MPSiteType type = MPSiteType.GeneratedMaximum;
        MPSiteVariant variant = MPSiteVariant.Password;
        if (typeAndVariant.length >= 1) {
            try {
                type = MPSiteType.valueOf(typeAndVariant[0]);
            } catch (Exception ex) {
                // type is already default of GeneratedMaximum
            }
        }
        if (typeAndVariant.length >= 2) {
            try {
                variant = MPSiteVariant.valueOf(typeAndVariant[1]);
            } catch (Exception ex) {
                // variant is already default of Password
            }
        }
        return new Pair<>(type, variant);
    }
}
