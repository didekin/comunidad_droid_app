package com.didekindroid.incidencia.firebase;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.TaskStackBuilder;
import android.util.ArrayMap;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.MockActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Map;

import static android.app.Notification.EXTRA_SUB_TEXT;
import static android.app.Notification.EXTRA_TEXT;
import static android.app.Notification.EXTRA_TITLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
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
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static com.didekinlib.model.common.gcm.GcmKeyValueData.type_message_key;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.comunidadId_key;
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

    MockActivity mActivity;
    long comunidadId;
    Map<String, String> data;
    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

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

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    =========================== TESTS =============================

    @Test
    public void testDoStackBuilder_INCIDENCIA_OPEN() throws Exception
    {
        TaskStackBuilder stackBuilder = INCIDENCIA_OPEN.doStackBuilder(mActivity, data);
        assertThat(stackBuilder.getIntentCount(), is(2));
        Intent[] intents = stackBuilder.getIntents();
        assertThat(intents[0].getComponent().getShortClassName(), is(".comunidad.ComuSearchAc"));
        assertThat(intents[1].getComponent().getShortClassName(), is(".incidencia.activity.IncidSeeOpenByComuAc"));
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

    @Test
    public void testProcessMsgWithHandler()   // TODO: terminar test.
    {
        /*RemoteMessage remoteMsg = new RemoteMessage.Builder(FirebaseInstanceId.getInstance().getToken())
                .addData(type_message_key, )
                .addData(comunidadId_key, comunidadId)*/
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
        assertThat(notification.extras.getCharSequence(EXTRA_TITLE).toString(), is(mActivity.getString(extrasRscId[0])));
        assertThat(notification.extras.getCharSequence(EXTRA_TEXT).toString(), is(mActivity.getString(extrasRscId[1])));
        assertThat(notification.extras.getCharSequence(EXTRA_SUB_TEXT).toString(), is(mActivity.getString(extrasRscId[2])));
    }

}