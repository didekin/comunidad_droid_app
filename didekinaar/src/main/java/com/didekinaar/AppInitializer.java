package com.didekinaar;

import android.content.Context;
import android.os.StrictMode;

import com.didekin.common.controller.JksInClient;
import com.didekin.common.controller.RetrofitHandler;
import com.didekinaar.security.JksInAndroidApp;

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
    private final Context mContext;
    private static final AtomicReference<RetrofitHandler> atomicRefRetrofit = new AtomicReference<>();

    public AppInitializer(Context appContext)
    {
        mContext = appContext;
        initDebugBuildConfig();
    }

    public Context getContext()
    {
        return mContext;
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
