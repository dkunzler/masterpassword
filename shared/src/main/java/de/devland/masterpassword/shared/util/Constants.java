package de.devland.masterpassword.shared.util;

import java.util.regex.Pattern;

/**
 * Created by deekay on 29/01/15.
 */
public interface Constants {

    Pattern pattern = Pattern.compile(
            "^" +
                    // protocol identifier
                    "(?:(?:https?|ftp)://)" +
                    "(www\\.)?([\\.a-z\\u00a1-\\uffff0-9]*)((:?)|(/?)).*"
    );

}
