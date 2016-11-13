package com.didekindroid.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.makeToast;

/**
 * User: pedro
 * Date: 17/02/15
 * Time: 12:37
 */
public class ConnectionUtils {

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
        Timber.d("isInternetConnected()");
        return isMobileConnected(context) || isWifiConnected(context);
    }

    private static boolean isMobileConnected(Context context)
    {
        Timber.d("isMobileConnected()");

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean isMobileConnected = (networkInfo != null && networkInfo.isConnected());

        if (isMobileConnected) {
            isMobileConnected = (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
        }

        Timber.d("isMobileConnected(): %b", isMobileConnected);
        return isMobileConnected;
    }

    private static boolean isWifiConnected(Context context)
    {
        Timber.d("isWifiConnected()");

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean isWifiConnected = networkInfo != null && networkInfo.isConnected();
        if (isWifiConnected) {
            isWifiConnected = (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
        }

        Timber.d("isWifiConnected(): %b", isWifiConnected);
        return isWifiConnected;
    }
}