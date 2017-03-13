package com.didekindroid;

import android.content.Context;
import android.os.StrictMode;

import com.didekindroid.exception.UiExceptionIf.UiExceptionRouterIf;
import com.didekindroid.security.JksInAndroidApp;
import com.didekinlib.http.JksInClient;
import com.didekinlib.http.retrofit.RetrofitHandler;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static java.lang.Integer.parseInt;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 14:21
 */
public final class AppInitializer {

    public static final AtomicReference<AppInitializer> creator = new AtomicReference<>();
    private static final AtomicReference<RetrofitHandler> atomicRefRetrofit = new AtomicReference<>();
    private final Context mContext;
    private final UiExceptionRouterIf exceptionDealer;

    public AppInitializer(Context appContext, UiExceptionRouterIf dealer)
    {
        mContext = appContext;
        exceptionDealer = dealer;
        initDebugBuildConfig();
    }

    public Context getContext()
    {
        return mContext;
    }

    public UiExceptionRouterIf getExceptionDealer()
    {
        return exceptionDealer;
    }

    /**
     * This method is called asynchronously from the (http) services classes, to avoid running it in the main thread,
     * since it reads from disk (raw resources).
     */
    public RetrofitHandler getRetrofitHandler()
    {
        atomicRefRetrofit.compareAndSet(null,
                new RetrofitHandler(
                        mContext.getString(R.string.didekinspring_host) + mContext.getString(R.string.didekinspring_port),
                        initJksInAndroidApp(),
                        parseInt(mContext.getString(R.string.timeOut))
                ));
        return atomicRefRetrofit.get();
    }

    //  ==================================  HELPERS ===================================

    private void initDebugBuildConfig()
    {
        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE.equals("local")) {
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
                    .detectLeakedRegistrationObjects()
                    .penaltyLog()
                    .build());

            Timber.d("BUILD_TYPE: %s", BuildConfig.BUILD_TYPE);
            /* TODO: ejemplo en Timber para librería de comunicación de errores en cliente al servidor.*/
        }
    }

    private JksInClient initJksInAndroidApp()
    {
        String bksPassword = mContext.getString(R.string.didekindroid_bks_pswd);
        int bksRawFileResourceId = mContext.getResources()
                .getIdentifier(mContext.getString(R.string.didekindroid_bks_name), "raw", mContext.getPackageName());

        if (bksPassword.isEmpty() || bksRawFileResourceId <= 0) {
            throw new IllegalStateException("BKS should be initialized in client application.");
        }
        return new JksInAndroidApp(bksPassword, bksRawFileResourceId);
    }
}
