package com.didekindroid;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public final class DidekindroidApp extends Application {

    private static Context mContext;
    private static String mBaseURL;
    private static String jksPassword;
    private static int jksResourceId;
    private static final String TAG = DidekindroidApp.class.getCanonicalName();

    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mContext = this;
        mBaseURL = mContext.getResources().getString(R.string.didekinspring_host)
                + getContext().getResources().getString(R.string.didekinspring_port);
        jksPassword = mContext.getResources().getString(R.string.didekinspring_pswd);
        jksResourceId = R.raw.didekindroid_pre_bks;  // TODO: cambiar en pre y pro.
    }

    public static Context getContext()
    {
        Log.d(TAG, "getContext()");
        return mContext;
    }

    public static String getBaseURL()
    {
        Log.d(TAG, "getBaseURL()");
        return mBaseURL;
    }

    public static String getJksPassword()
    {
        Log.d(TAG, "getJksPassword()");
        return jksPassword;
    }

    public static int getJksResourceId()
    {
        Log.d(TAG, "getJksResourceId()");
        return jksResourceId;
    }
}
