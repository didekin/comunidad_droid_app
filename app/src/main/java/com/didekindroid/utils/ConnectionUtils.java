package com.didekindroid.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * User: pedro
 * Date: 17/02/15
 * Time: 12:37
 */
public class ConnectionUtils {

    private static final String TAG = ConnectionUtils.class.getCanonicalName();

    public static boolean isInternetConnected(Context context)
    {
        Log.d(TAG, "isInternetConnected()");
        return isMobileConnected(context) || isWifiConnected(context);
    }

    /**
     * Check whether the device is connected.
     *
     * @param context
     */
    static boolean isMobileConnected(Context context)
    {
        Log.d(TAG, "isMobileConnected()");

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean isMobileConnected = (networkInfo != null && networkInfo.isConnected());

        if (isMobileConnected) {
            isMobileConnected = (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        }

        Log.d(TAG, "isMobileConnected(): " + isMobileConnected);
        return isMobileConnected;
    }

    static boolean isWifiConnected(Context context)
    {
        Log.d(TAG, "isWifiConnected()");

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean isWifiConnected = networkInfo != null && networkInfo.isConnected();
        if (isWifiConnected) {
            isWifiConnected = (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        }

        Log.d(TAG, "isWifiConnected(): " + isWifiConnected);
        return isWifiConnected;
    }

}