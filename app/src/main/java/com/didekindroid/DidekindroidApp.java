package com.didekindroid;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public class DidekindroidApp extends Application {

    private static Context mContext;
    private static final String TAG = DidekindroidApp.class.getCanonicalName();

    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG,"onCreate()");
        mContext = this;
    }

    public static Context getContext()
    {
        Log.d(TAG,"getContext()");
        return mContext;
    }
}
