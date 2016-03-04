package com.didekindroid.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.makeToast;

/**
 * User: pedro
 * Date: 17/02/15
 * Time: 12:37
 */
public class ConnectionUtils {

    private static final String TAG = ConnectionUtils.class.getCanonicalName();

    public static boolean checkInternetConnected(Context context)
    {
        if (!isInternetConnected(context)) {
            makeToast(context, R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    public static boolean isInternetConnected(Context context)
    {
        Log.d(TAG, "isInternetConnected()");
        return isMobileConnected(context) || isWifiConnected(context);
    }

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