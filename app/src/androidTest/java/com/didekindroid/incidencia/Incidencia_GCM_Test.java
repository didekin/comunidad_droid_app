package com.didekindroid.incidencia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.IdlingResourceForIntentServ;
import com.didekindroid.exception.UiAppException;
import com.didekinaar.usuario.AarFBRegIntentService;
import com.didekinservice.common.gcm.GcmRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.security.TokenIdentityCacher.isGcmTokenSentServer;
import static com.didekinaar.security.TokenIdentityCacher.updateIsGcmTokenSentServer;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public abstract class Incidencia_GCM_Test {

    Activity mActivity;
    IdlingResourceForIntentServ idlingResource;
    NotificationManager mNotifyManager;

    @Rule
    public IntentsTestRule<? extends Activity> intentRule = doIntentsTestRule();

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        mNotifyManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        idlingResource = new IdlingResourceForIntentServ(mActivity, new AarFBRegIntentService());
        Espresso.registerIdlingResources(idlingResource);
    }

    @After
    public void tearDown() throws Exception
    {
        updateIsGcmTokenSentServer(false, mActivity);
        mNotifyManager.cancelAll();
        Espresso.unregisterIdlingResources(idlingResource);
        cleanOptions(CLEAN_PEPE);
    }

    //  ===========================================================================

    protected abstract IntentsTestRule<? extends Activity> doIntentsTestRule();

    void checkToken() throws UiAppException, InterruptedException, UiException
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        assertThat(refreshedToken, notNullValue());
        Thread.sleep(2000);
        assertThat(isGcmTokenSentServer(mActivity), is(true));
        assertThat(usuarioDaoRemote.getGcmToken(), is(refreshedToken));
    }

    @TargetApi(Build.VERSION_CODES.M)
    void checkNotification(int typeMsgInt) throws InterruptedException
    {
        NotificationManager mManager = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        // Verifico recepción de notificación.
        Thread.sleep(3000);
        assertThat(mManager.getActiveNotifications().length, is(1));
        StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(typeMsgInt));

        // We check the pending intent.
        PendingIntent pendingIntent = barNotification.getNotification().contentIntent;
        assertThat(pendingIntent.getCreatorPackage(), is(GcmRequest.PACKAGE_DIDEKINDROID));
    }
}