package com.didekindroid.incidencia.firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.MockActivity;
import com.didekinlib.gcm.model.common.GcmException;
import com.didekinlib.gcm.model.common.GcmRequest;
import com.didekinlib.gcm.model.common.GcmResponse;
import com.didekinlib.gcm.model.common.GcmSingleRequest;
import com.didekinlib.gcm.model.incidservice.GcmIncidRequestData;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.PACKAGE_TEST;
import static com.didekindroid.incidencia.testutils.GcmConstantForTests.test_api_key_header;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.gcm.model.common.GcmServConstant.IDENTITY;
import static com.didekinlib.model.incidencia.gcm.GcmKeyValueIncidData.incidencia_open_type;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmIncidAltaNotificationTest extends GcmIncidNotificationTest {

    IncidenciaUser incidenciaUser;

    @Override
    protected IntentsTestRule<MockActivity> doIntentsTestRule()
    {
        return new IntentsTestRule<MockActivity>(MockActivity.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                try {
                    signUpAndUpdateTk(COMU_REAL_PEPE);
                    UsuarioComunidad pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
                    comunidadIdIntent = pepeUserComu.getComunidad().getC_Id();
                    incidenciaUser = insertGetIncidenciaUser(pepeUserComu, 1);
                } catch (IOException | UiException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //    =========================== TESTS =============================

    /**
     * Sinqle tokenId request.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_1() throws IOException, InterruptedException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GcmSingleRequest request = new GcmSingleRequest.Builder(gcmToken,
                    new GcmRequest.Builder(new GcmIncidRequestData(incidencia_open_type, comunidadIdIntent), PACKAGE_TEST).build())
                    .build();
            GcmResponse gcmResponse = endPointImp.sendGcmSingleRequest(IDENTITY, test_api_key_header, request).execute().body();
            assertThat(gcmResponse.getSuccess(), is(1));
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA_OPEN.getBarNotificationId()));
            assertThat(barNotification.getPackageName(), is(PACKAGE_TEST));
            assertThat(barNotification.getNotification().extras.getString(Notification.EXTRA_TEXT), is(context.getString(INCIDENCIA_OPEN.getBarNotificationId())));
        }
    }

    /**
     * Multicast request with two tokenIds.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_2() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GcmResponse gcmResponse = endPointImp.sendMulticastGzip(test_api_key_header, getGcmMultiRequest(incidencia_open_type));
            assertThat(gcmResponse.getSuccess(), is(2));
            Thread.sleep(2000);
            assertThat(mManager.getActiveNotifications().length, is(1));
            StatusBarNotification barNotification = mManager.getActiveNotifications()[0];
            assertThat(barNotification.getId(), is(INCIDENCIA_OPEN.getBarNotificationId()));

            // We check the pending intent.
            PendingIntent pendingIntent = barNotification.getNotification().contentIntent;
            assertThat(pendingIntent.getCreatorPackage(), is(PACKAGE_TEST));
        }
    }

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with UP.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_3() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendMulticastGzip(test_api_key_header, getGcmMultiRequest(incidencia_open_type));
            Thread.sleep(2000);
            Notification notification = mManager.getActiveNotifications()[0].getNotification();
            final PendingIntent pendingIntent = notification.contentIntent;

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

            onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
            // Verificamos que se muestran los datos de las incidencias abiertas.
            onView(allOf(
                    withId(R.id.incid_see_importancia_block),
                    hasSibling(allOf(
                            withText(incidenciaUser.getIncidencia().getDescripcion()),
                            withId(R.id.incid_descripcion_view)
                    )),
                    hasSibling(allOf(
                            withId(R.id.incid_see_apertura_block),

                            hasDescendant(allOf(
                                    withId(R.id.incid_see_iniciador_view),
                                    withText(incidenciaUser.getUsuario().getAlias())
                            ))
                    ))
            )).check(matches(isDisplayed()));
            clickNavigateUp();
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        }
    }

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with BACK.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_4() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendMulticastGzip(test_api_key_header, getGcmMultiRequest(incidencia_open_type));
            Thread.sleep(2000);
            final PendingIntent pendingIntent = mManager.getActiveNotifications()[0].getNotification().contentIntent;

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

            onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        }
    }
}
