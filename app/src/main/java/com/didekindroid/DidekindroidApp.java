package com.didekindroid;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.didekin.common.controller.RetrofitHandler;
import com.didekindroid.common.webservices.JksInAndroidApp;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static java.lang.Integer.parseInt;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public final class DidekindroidApp extends Application {

    private static final AtomicReference<Context> mContext = new AtomicReference<>();
    private static final AtomicInteger httpTimeOut = new AtomicInteger();
    private static final AtomicReference<RetrofitHandler> retrofitHandler = new AtomicReference<>();

    public void onCreate()
    {
        super.onCreate();

        if (BuildConfig.DEBUG) {

            Timber.plant(new Timber.DebugTree());
//            ButterKnife.setDebug(true);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        // TODO: ejemplo en Timber para librería de comunicación de errores en cliente al servidor.

        mContext.compareAndSet(null, this);
        httpTimeOut.compareAndSet(0, parseInt(getString(R.string.timeOut)));
    }

    /**
     *   This method is called asynchronously from the (http) services classes, to avoid running it in the main thread,
     *   since it reads from disk (raw resources).
     */
    public static void initRetrofitHandler()
    {
        retrofitHandler.compareAndSet(
                null,
                new RetrofitHandler(
                        mContext.get().getString(R.string.didekinspring_host) + mContext.get().getString(R.string.didekinspring_port),
                        new JksInAndroidApp(
                                mContext.get().getString(R.string.didekinspring_pswd),
                                mContext.get().getResources().getIdentifier(mContext.get().getString(R.string.bks_name), "raw", mContext.get().getPackageName())
                        ),
                        parseInt(mContext.get().getString(R.string.timeOut)))
        );
    }

    public static Context getContext()
    {
        return mContext.get();
    }

    public static int getHttpTimeOut()
    {
        return httpTimeOut.get();
    }

    public static RetrofitHandler getRetrofitHandler()
    {
        return retrofitHandler.get();
    }
}
