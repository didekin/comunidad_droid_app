package com.didekindroid.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * User: pedro@didekin
 * Date: 04/06/15
 * Time: 17:39
 */
@SuppressWarnings("unused")
public class DeviceUtil {

    private static int getWidthDevice(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private static int getHightDevice(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static boolean is768by1232device(Activity activity)
    {
        return (getWidthDevice(activity) == 768 && getHightDevice(activity) == 1232);
    }

    static String getAppLanguage()
    {
        return Locale.getDefault().getLanguage();
    }

    static String getDeviceLanguage()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Resources.getSystem().getConfiguration().getLocales().get(0).toString();
        } else {
            //noinspection deprecation
            return Resources.getSystem().getConfiguration().locale.toString();
        }
    }
}

/* To get the screen size in inches, one we get width and height:
int dens=dm.densityDpi;
double wi=(double)width/(double)dens;
double hi=(double)height/(double)dens;
double x = Math.pow(wi,2);
double y = Math.pow(hi,2);
double screenInches = Math.sqrt(x+y);
*/