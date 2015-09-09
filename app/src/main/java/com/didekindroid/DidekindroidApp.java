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
    private static final String TAG = DidekindroidApp.class.getCanonicalName();

    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mContext = this;
        mBaseURL = new StringBuilder(mContext.getResources().getString(R.string.service_one_host))
                .append(getContext().getResources().getString(R.string.service_one_port))
                .toString();
    }

    public final static Context getContext()
    {
        Log.d(TAG, "getContext()");
        return mContext;
    }

    public final static String getBaseURL()
    {
        Log.d(TAG, "getBaseURL()");
        return mBaseURL;
    }
}
