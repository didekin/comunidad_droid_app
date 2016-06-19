package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.MockActivity;
import com.didekindroid.common.activity.UiException;
import com.didekinservice.common.gcm.GcmException;

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
import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.resolucion_open_type;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmIncidResolucinOpenNotifTest extends GcmIncidNotificationTest {

    private IncidenciaUser incidenciaUser;

    @Override
    protected IntentsTestRule<MockActivity> doIntentsTestRule()
    {
        return new IntentsTestRule<MockActivity>(MockActivity.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                try {
                    signUpAndUpdateTk(COMU_REAL_PEPE);
                    UsuarioComunidad pepeUserComu = ServOne.seeUserComusByUser().get(0);
                    comunidadIdIntent = pepeUserComu.getComunidad().getC_Id();
                    incidenciaUser = insertGetIncidenciaUser(pepeUserComu, 1);
                } catch (UiException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    //    =========================== TESTS =============================

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with UP.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_1() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest(resolucion_open_type));
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

            checkNavigateUp();
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    }

    /**
     * Multicast request with two tokenIds.
     * We check the backStack with BACK.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testCheckNotification_2() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest(resolucion_open_type));
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
            onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        }
    }

//    ===============================  HELPER METHODS ===================================
}
