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

import com.didekindroid.ManagerMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.ControllerFirebaseToken;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.PACKAGE_TEST;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 *
 * Integration tests for Firebase messages:
 * 1. We check that the firebase token is sent to server when the activity is created.
 * 2. We check that after a task which produces a notification, a notification is received in the phone.
 */
@RunWith(AndroidJUnit4.class)
public abstract class Incidencia_GCM_Test {

    Activity mActivity;
    NotificationManager notificationManager;
    String firebaseToken;

    /**
     * To be implemented in subclasses.
     */
    protected abstract IntentsTestRule<? extends Activity> doIntentsTestRule();

    @Rule
    public IntentsTestRule<? extends Activity> intentRule = doIntentsTestRule();

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        notificationManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @After
    public void tearDown() throws Exception
    {
        notificationManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

    //  ===========================================================================

    protected void checkToken() throws InterruptedException, UiException
    {
        ControllerFirebaseTokenIf controller = new ControllerFirebaseToken(newViewerFirebaseToken(new ManagerMock(mActivity)));
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        await().atMost(6, SECONDS).until(getGcmToken(), allOf(
                notNullValue(),
                is(firebaseToken)
        ));
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void checkNotification(int notificationId) throws InterruptedException
    {
        // Verifico recepción de notificación.
        await().atMost(5, SECONDS).until(notificationsSize(), is(1));

        StatusBarNotification barNotification = notificationManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(notificationId));
        // We check the pending intent.
        assertThat(barNotification.getNotification().contentIntent.getCreatorPackage(), is(PACKAGE_TEST));
    }

    /* ........................Awaitility helpers ................ */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Callable<Integer> notificationsSize() {
        return new Callable<Integer>() {
            public Integer call() throws Exception {
                notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
                return notificationManager.getActiveNotifications().length;
            }
        };
    }

    private Callable<String> getGcmToken() {
        return new Callable<String>() {
            public String call() throws Exception {
                return usuarioDao.getGcmToken();
            }
        };
    }
}