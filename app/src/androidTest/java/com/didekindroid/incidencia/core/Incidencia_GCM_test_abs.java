package com.didekindroid.incidencia.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyToken;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenIf;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.didekindroid.incidencia.testutils.GcmTestConstant.PACKAGE_TEST;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.google.firebase.iid.FirebaseInstanceId.getInstance;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Integration tests for Firebase messages:
 * 1. We check that the firebase token is sent to server when the activity is created.
 * 2. We check that after a task which produces a notification, a notification is received in the phone.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public abstract class Incidencia_GCM_test_abs {

    @Rule
    public IntentsTestRule<? extends Activity> intentRule = doIntentsTestRule();
    private Activity mActivity;
    private NotificationManager notificationManager;

    /**
     * To be implemented in subclasses.
     */
    protected abstract IntentsTestRule<? extends Activity> doIntentsTestRule();

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        // Double check.
        notificationManager.cancelAll();
    }

    @After
    public void tearDown() throws Exception
    {
        notificationManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

    //  ===========================================================================

    protected void checkToken()
    {
        CtrlerNotifyTokenIf controller = new CtrlerNotifyToken();
        await().atMost(12, SECONDS)
                .until(() -> usuarioDaoRemote.getGcmToken().blockingGet().equals(getInstance().getToken()));
        assertThat(controller.getTkCacher().isGcmTokenSentServer(), is(true));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void checkNotification(int notificationId)
    {
        // Verifico recepción de notificación.
        waitAtMost(14, SECONDS).until(notificationsSize(), is(1));

        StatusBarNotification barNotification = notificationManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(notificationId));
        // We check the pending intent.
        assertThat(barNotification.getNotification().contentIntent.getCreatorPackage(), is(PACKAGE_TEST));
    }

    /* ........................Awaitility helpers ................ */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Callable<Integer> notificationsSize()
    {
        return () -> {
            notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
            return notificationManager.getActiveNotifications().length;
        };
    }
}