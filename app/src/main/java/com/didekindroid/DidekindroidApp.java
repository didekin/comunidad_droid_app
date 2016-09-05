package com.didekindroid;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

import static java.lang.Integer.parseInt;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
@SuppressWarnings({"StaticVariableMayNotBeInitialized", "StaticVariableUsedBeforeInitialization"})
public final class DidekindroidApp extends Application {

    private static Context mContext;
    private static String mBaseURL;
    private static String jksPassword;
    private static int jksResourceId;
    private static int httpTimeOut;

    public void onCreate()
    {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        // TODO: ejemplo en Timber para librería de comunicación de errores en cliente.

        mContext = this;
        mBaseURL = mContext.getString(R.string.didekinspring_host)
                + mContext.getString(R.string.didekinspring_port);
        httpTimeOut = parseInt(mContext.getString(R.string.timeOut));
        jksPassword = mContext.getString(R.string.didekinspring_pswd);
        jksResourceId = mContext.getResources().getIdentifier(mContext.getString(R.string.bks_name), "raw", mContext.getPackageName());
    }

    public static Context getContext()
    {
        Timber.d("getContext()");
        return mContext;
    }

    public static String getBaseURL()
    {
        Timber.d("getBaseURL()");
        return mBaseURL;
    }

    public static String getJksPassword()
    {
        Timber.d("getJksPassword()");
        return jksPassword;
    }

    public static int getJksResourceId()
    {
        Timber.d("getJksResourceId()");
        return jksResourceId;
    }

    public static int getHttpTimeOut()
    {
        return httpTimeOut;
    }
}
