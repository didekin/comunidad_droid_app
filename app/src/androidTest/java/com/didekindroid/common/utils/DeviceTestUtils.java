package com.didekindroid.common.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * User: pedro@didekin
 * Date: 04/06/15
 * Time: 17:39
 */
@SuppressWarnings("unused")
public class DeviceTestUtils {

    public static int getWidthDevice(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getHightDevice(Activity activity)
    {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static boolean is768by1232device(Activity activity)
    {
        return (getWidthDevice(activity) == 768 && getHightDevice(activity) == 1232);
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