package com.didekindroid;

import android.app.Activity;
import android.app.Application;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.lib_one.FirebaseInitializer;
import com.didekindroid.lib_one.HttpInitializer;
import com.didekindroid.lib_one.RouterInitializer;
import com.didekindroid.lib_one.api.router.ContextualRouter;
import com.didekindroid.lib_one.api.router.MnRouter;
import com.didekindroid.lib_one.api.router.UiExceptionRouter;
import com.didekindroid.lib_one.security.SecInitializer;

import timber.log.Timber;

import static com.didekindroid.BuildConfig.BUILD_TYPE;
import static com.didekindroid.lib_one.FirebaseInitializer.firebaseInitializer;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.router.DidekinContextAction.didekinContextAcMap;
import static com.didekindroid.router.DidekinMnAction.didekinMnItemMap;
import static com.didekindroid.router.DidekinUiExceptionAction.didekinExcpMsgMap;
import static io.reactivex.internal.functions.Functions.emptyConsumer;
import static io.reactivex.plugins.RxJavaPlugins.setErrorHandler;


/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 19:19
 */
public final class DidekinApp extends Application {

    private static final String webHost = BuildConfig.didekin_web_host;
    private static final String webHostPort = BuildConfig.didekin_web_port;
    private static final int timeOut = R.string.timeOut;
    private static final int bks_pswd = R.string.didekindroid_bks_pswd;
    private static final int bks_name = R.string.didekindroid_bks_name;
    public static final Class<? extends Activity> defaultAc = ComuSearchAc.class;

    // This is the project number in google-services.json.
    static final String APP_PROJECT_ID = "61369502868";
    // Scope used to get a token.
    static final String SCOPE_TOKEN = "FCM";

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
                .contexRouter(new ContextualRouter(didekinContextAcMap))
                .exceptionRouter(new UiExceptionRouter(didekinExcpMsgMap))
                .mnRouter(new MnRouter(didekinMnItemMap))
                .defaultAc(defaultAc)
                .build());
        firebaseInitializer.compareAndSet(null, new FirebaseInitializer(APP_PROJECT_ID, SCOPE_TOKEN));
        // To avoid closing the application for the default Android uncaught exception handler.
        setErrorHandler(emptyConsumer());
    }

    @SuppressWarnings("ConstantConditions")
    private void initDebugBuildConfig()
    {
        if (BUILD_TYPE.equalsIgnoreCase("local") || BUILD_TYPE.equalsIgnoreCase("pre")) {

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

            Timber.d("================= initDebugBuildConfig(), BUILD_TYPE: %s ===================", BUILD_TYPE);
        }
        // TODO: ejemplo en Timber para librería de comunicación de errores en cliente al servidor.
    }
}
