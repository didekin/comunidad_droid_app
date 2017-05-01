package com.didekindroid.incidencia.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.TaskStackBuilder;
import android.util.ArrayMap;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import static android.app.Notification.EXTRA_SUB_TEXT;
import static android.app.Notification.EXTRA_TEXT;
import static android.app.Notification.EXTRA_TITLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.R.string.gcm_message_generic_subtext;
import static com.didekindroid.R.string.incid_gcm_incidencia_closed_body;
import static com.didekindroid.R.string.incid_gcm_nueva_incidencia_body;
import static com.didekindroid.R.string.incid_gcm_resolucion_open_body;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.INCIDENCIA_CLOSE;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.RESOLUCION_OPEN;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.processMsgWithHandler;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.PACKAGE_TEST;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekinlib.model.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.comunidadId_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_closed_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.resolucion_open_type;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.fail;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/01/17
 * Time: 11:17
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidFirebaseDownMsgHandlerTest {

    ActivityMock mActivity;
    long comunidadId;
    Map<String, String> data;
    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                comunidadId = signUpWithTkGetComu(COMU_REAL_JUAN).getC_Id();
                data = new ArrayMap<>(1);
                data.put(comunidadId_key, String.valueOf(comunidadId));
            } catch (IOException | UiException e) {
                e.printStackTrace();
            }
        }
    };
    NotificationManager notificationManager;

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        SECONDS.sleep(2);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    //  ============================= UNIT TESTS ==============================

    @Test
    public void testDoStackBuilder_INCIDENCIA_OPEN() throws Exception
    {
        TaskStackBuilder stackBuilder = INCIDENCIA_OPEN.doStackBuilder(mActivity, data);
        assertThat(stackBuilder.getIntentCount(), is(2));
        Intent[] intents = stackBuilder.getIntents();
        assertThat(intents[0].getComponent().getShortClassName(), is(".comunidad.ComuSearchAc"));
        assertThat(intents[1].getComponent().getShortClassName(), is(".incidencia.list.open.IncidSeeOpenByComuAc"));
        assertThat(intents[1].getLongExtra(COMUNIDAD_ID.key, 0L), is(comunidadId));
    }

    @Test
    public void testPendingIntent_INCIDENCIA_OPEN() throws Exception
    {
        final PendingIntent pendingIntent = INCIDENCIA_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, R.id.incid_see_open_by_comu_ac, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testPendingIntent_INCIDENCIA_CLOSE() throws Exception
    {
        final PendingIntent pendingIntent = INCIDENCIA_CLOSE.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, R.id.incid_see_closed_by_comu_ac, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testPendingIntent_RESOLUCION_OPEN() throws Exception
    {
        final PendingIntent pendingIntent = RESOLUCION_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, R.id.incid_see_open_by_comu_ac, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testDoNotification_INCIDENCIA_OPEN() throws Exception
    {
        Notification notification = INCIDENCIA_OPEN
                .doNotification(mActivity, INCIDENCIA_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_nueva_incidencia_body, incid_gcm_nueva_incidencia_body, gcm_message_generic_subtext);

    }

    @Test
    public void testDoNotification_INCIDENCIA_CLOSE() throws Exception
    {
        Notification notification = INCIDENCIA_CLOSE
                .doNotification(mActivity, INCIDENCIA_CLOSE.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_incidencia_closed_body, incid_gcm_incidencia_closed_body, gcm_message_generic_subtext);

    }

    @Test
    public void testDoNotification_RESOLUCION_OPEN() throws Exception
    {
        Notification notification = RESOLUCION_OPEN
                .doNotification(mActivity, RESOLUCION_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_resolucion_open_body, incid_gcm_resolucion_open_body, gcm_message_generic_subtext);

    }

    //    ====================== INTEGRATION TESTS =========================

    /**
     * 1. We build a RemoteMessage instancea and we pass it to the handler.
     * 2. We check that a notification is received.
     */
    @Test
    public void testProcessMsgWithHandler_INCIDENCIA_OPEN()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        RemoteMessage remoteMsg = new RemoteMessage.Builder("to ME")
                .addData(type_message_key, incidencia_open_type)
                .addData(comunidadId_key, String.valueOf(comunidadId))
                .setCollapseKey(incidencia_open_type)
                .build();

        processMsgWithHandler(remoteMsg, mActivity);
        StatusBarNotification barNotification = checkBarNotification(INCIDENCIA_OPEN);
        checkNotification(barNotification.getNotification(), incid_gcm_nueva_incidencia_body);
    }

    @Test
    public void testProcessMsgWithHandler_INCIDENCIA_CLOSE()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        RemoteMessage remoteMsg = new RemoteMessage.Builder("to ME")
                .addData(type_message_key, incidencia_closed_type)
                .addData(comunidadId_key, String.valueOf(comunidadId))
                .setCollapseKey(incidencia_closed_type)
                .build();

        processMsgWithHandler(remoteMsg, mActivity);
        StatusBarNotification barNotification = checkBarNotification(INCIDENCIA_CLOSE);
        checkNotification(barNotification.getNotification(), incid_gcm_incidencia_closed_body);
    }

    @Test
    public void testProcessMsgWithHandler_RESOLUCION_OPEN()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        RemoteMessage remoteMsg = new RemoteMessage.Builder("to ME")
                .addData(type_message_key, resolucion_open_type)
                .addData(comunidadId_key, String.valueOf(comunidadId))
                .setCollapseKey(resolucion_open_type)
                .build();

        processMsgWithHandler(remoteMsg, mActivity);
        StatusBarNotification barNotification = checkBarNotification(RESOLUCION_OPEN);
        checkNotification(barNotification.getNotification(), incid_gcm_resolucion_open_body);
    }

    //    =========================== HELPERS =============================

    private void checkUiPendingIntent(final PendingIntent pendingIntent, int... layouts)
    {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        });

        onView(withId(layouts[0])).check(matches(isDisplayed()));
        clickNavigateUp();
        // Verificamos que se muestran los datos de búsqueda de comunidad.
        onView(withId(layouts[1])).check(matches(isDisplayed()));
    }

    private void checkNotification(Notification notification, int... extrasRscId)
    {
        int switchInt = extrasRscId.length;
        switch (switchInt) {
            case 3:
                assertThat(notification.extras.getCharSequence(EXTRA_SUB_TEXT).toString(), is(mActivity.getString(extrasRscId[2])));
            case 2:
                assertThat(notification.extras.getCharSequence(EXTRA_TEXT).toString(), is(mActivity.getString(extrasRscId[1])));
            case 1:
                assertThat(notification.extras.getCharSequence(EXTRA_TITLE).toString(), is(mActivity.getString(extrasRscId[0])));
                break;
            default:
                fail();
        }
    }


    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.M)
    private StatusBarNotification checkBarNotification(IncidFirebaseDownMsgHandler handler)
    {
        waitAtMost(6, SECONDS).until(notificationsSize(), is(1));
        StatusBarNotification barNotification = notificationManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(handler.getBarNotificationId()));
        assertThat(barNotification.getPackageName(), is(PACKAGE_TEST));
        return barNotification;
    }

    /* ........................Awaitility helpers ................ */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Callable<Integer> notificationsSize()
    {
        return new Callable<Integer>() {
            public Integer call() throws Exception
            {
                notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
                return notificationManager.getActiveNotifications().length;
            }
        };
    }
}