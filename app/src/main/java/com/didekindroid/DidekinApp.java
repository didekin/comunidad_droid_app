package com.didekindroid;

import android.app.Application;
import android.app.FragmentManager;
import android.os.StrictMode;

import com.didekindroid.lib_one.HttpInitializer;
import com.didekindroid.lib_one.security.SecInitializer;
import com.didekindroid.lib_one.RouterInitializer;

import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.router.ContextualRouter.context_router;
import static com.didekindroid.router.MnRouter.mn_router;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;
import static io.reactivex.internal.functions.Functions.emptyConsumer;
import static io.reactivex.plugins.RxJavaPlugins.setErrorHandler;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public final class DidekinApp extends Application {

    public static final int webHost = R.string.didekin_web_host;
    public static final int webHostPort = R.string.didekin_web_port;
    public static final int timeOut = R.string.timeOut;
    public static final int bks_pswd = R.string.didekindroid_bks_pswd;
    public static final int bks_name = R.string.didekindroid_bks_name;

    @Override
    public void onCreate()
    {
        super.onCreate();
        initDebugBuildConfig();
        secInitializer.compareAndSet(null, new SecInitializer(getApplicationContext(), bks_pswd, bks_name));
        httpInitializer.compareAndSet(
                null,
                new HttpInitializer.HttpInitializerBuilder(getApplicationContext())
                        .webHostAndPort(webHost, webHostPort)
                        .timeOut(timeOut)
                        .jksInClient(secInitializer.get().getJksInClient())
                        .build()
        );
        routerInitializer.compareAndSet(null, new RouterInitializer.RouterInitializerBuilder()
                .contexRouter(context_router)
                .exceptionRouter(uiException_router)
                .mnRouter(mn_router)
                .build());
        // To avoid closing the application for the default Android uncaught exception handler.
        setErrorHandler(emptyConsumer());
    }

    private void initDebugBuildConfig()
    {
        if (BuildConfig.DEBUG || BuildConfig.BUILD_TYPE.equals("local")) {

            Timber.plant(new Timber.DebugTree());

            FragmentManager.enableDebugLogging(true);

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

            Timber.d("initDebugBuildConfig(), BUILD_TYPE: %s", BuildConfig.BUILD_TYPE);
        }
        // TODO: ejemplo en Timber para librería de comunicación de errores en cliente al servidor.
    }
}
