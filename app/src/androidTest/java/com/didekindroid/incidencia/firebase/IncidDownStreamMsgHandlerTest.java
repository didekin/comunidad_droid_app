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
import com.didekindroid.lib_one.api.ActivityMock;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_CLOSE;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.RESOLUCION_OPEN;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.processMsgWithHandler;
import static com.didekindroid.incidencia.testutils.GcmTestConstant.PACKAGE_TEST;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.clickNavigateUp;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static com.didekinlib.gcm.GcmKeyValueData.type_message_key;
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
public class IncidDownStreamMsgHandlerTest {

    private ActivityMock mActivity;
    private long comunidadId;
    private Map<String, String> data;
    private NotificationManager notificationManager;

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                comunidadId = signUpGetComu(COMU_REAL_JUAN).getC_Id();
            } catch (Exception e) {
                fail();
            }
            data = new ArrayMap<>(1);
            data.put(comunidadId_key, String.valueOf(comunidadId));
        }
    };

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationsSize().call() > 0) {
                notificationManager.cancelAll();
            }
            waitAtMost(4, SECONDS).until(notificationsSize(), is(0));
        }
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    //  ============================= UNIT TESTS ==============================

    @Test
    public void testDoStackBuilder_INCIDENCIA_OPEN()
    {
        TaskStackBuilder stackBuilder = INCIDENCIA_OPEN.doStackBuilder(mActivity, data);
        assertThat(stackBuilder.getIntentCount(), is(2));
        Intent[] intents = stackBuilder.getIntents();
        assertThat(intents[0].getComponent().getShortClassName(), is(".comunidad.ComuSearchAc"));
        assertThat(intents[1].getComponent().getShortClassName(), is(".incidencia.list.IncidSeeByComuAc"));
        assertThat(intents[1].getLongExtra(COMUNIDAD_ID.key, 0L), is(comunidadId));
    }

    @Test
    public void testPendingIntent_INCIDENCIA_OPEN()
    {
        final PendingIntent pendingIntent = INCIDENCIA_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, incidSeeByComuAcLayout, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testPendingIntent_INCIDENCIA_CLOSE()
    {
        final PendingIntent pendingIntent = INCIDENCIA_CLOSE.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, R.id.incid_see_by_comu_ac, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testPendingIntent_RESOLUCION_OPEN()
    {
        final PendingIntent pendingIntent = RESOLUCION_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT);
        checkUiPendingIntent(pendingIntent, incidSeeByComuAcLayout, R.id.comu_search_ac_linearlayout);
    }

    @Test
    public void testDoNotification_INCIDENCIA_OPEN()
    {
        Notification notification = INCIDENCIA_OPEN
                .doNotification(mActivity, INCIDENCIA_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_nueva_incidencia_body, incid_gcm_nueva_incidencia_body, gcm_message_generic_subtext);

    }

    @Test
    public void testDoNotification_INCIDENCIA_CLOSE()
    {
        Notification notification = INCIDENCIA_CLOSE
                .doNotification(mActivity, INCIDENCIA_CLOSE.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_incidencia_closed_body, incid_gcm_incidencia_closed_body, gcm_message_generic_subtext);

    }

    @Test
    public void testDoNotification_RESOLUCION_OPEN()
    {
        Notification notification = RESOLUCION_OPEN
                .doNotification(mActivity, RESOLUCION_OPEN.doStackBuilder(mActivity, data).getPendingIntent(0, FLAG_UPDATE_CURRENT));
        checkNotification(notification, incid_gcm_resolucion_open_body, incid_gcm_resolucion_open_body, gcm_message_generic_subtext);

    }

    //    ====================== INTEGRATION TESTS =========================

    /**
     * 1. We build a RemoteMessage instancea and we pass it to the handler.
     * 2. We checkMenu that a notification is received.
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
        mActivity.runOnUiThread(() -> {
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
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
    private StatusBarNotification checkBarNotification(IncidDownStreamMsgHandler handler)
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
        return () -> {
            notificationManager = (NotificationManager) mActivity.getSystemService(NOTIFICATION_SERVICE);
            return notificationManager.getActiveNotifications().length;
        };
    }
}