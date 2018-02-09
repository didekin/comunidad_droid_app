package com.didekindroid.lib_one.util;

import android.os.Build;

import static android.content.res.Resources.getSystem;

/**
 * User: pedro@didekin
 * Date: 06/11/2017
 * Time: 14:58
 */

public class Device {

    public static String getDeviceLanguage()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getSystem().getConfiguration().getLocales().get(0).toString();
        } else {
            //noinspection deprecation
            return getSystem().getConfiguration().locale.toString();
        }
    }
}
