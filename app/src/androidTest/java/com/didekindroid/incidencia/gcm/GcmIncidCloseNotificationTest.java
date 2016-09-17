package com.didekindroid.incidencia.gcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
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
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.incidservice.gcm.GcmKeyValueIncidData.incidencia_closed_type;
import static com.didekindroid.common.testutils.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_PEPE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 12:21
 */
@RunWith(AndroidJUnit4.class)
public class GcmIncidCloseNotificationTest extends GcmIncidNotificationTest {

    private Incidencia incidencia;
    private Resolucion resolucion;
    IncidImportancia incidImportancia;

    @Override
    protected IntentsTestRule<MockActivity> doIntentsTestRule()
    {
        return new IntentsTestRule<MockActivity>(MockActivity.class) {
            /**
             * Preconditions:
             * 1. A user WITH powers 'adm' in sesssion.
             * 2. A resolucion in BD.
             * 3. Resolucion WITHOUT avances.
             * */
            @Override
            protected void beforeActivityLaunched()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_PEPE);
                    Thread.sleep(1000);
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                    assertThat(IncidenciaServ.closeIncidencia(resolucion), is(2));
                    incidencia = IncidenciaServ.seeIncidsClosedByComu(incidImportancia.getIncidencia().getComunidadId()).get(0).getIncidencia();
                } catch (UiException | InterruptedException | IOException e) {
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
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest(incidencia_closed_type));
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

            // Lista de incidencias cerradas.
            onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));
            // Verificamos que se muestran los datos de las incidencias cerradas.
            onView(allOf(
                    withId(R.id.incid_see_apertura_block),
                    hasDescendant(allOf(
                            withId(R.id.incid_fecha_alta_view),
                            withText(formatTimeStampToString(incidencia.getFechaAlta()))
                    )),
                    hasSibling(allOf(
                            withId(R.id.incid_see_cierre_block),
                            hasDescendant(allOf(
                                    withId(R.id.incid_fecha_cierre_view),
                                    withText(formatTimeStampToString(incidencia.getFechaCierre()))
                            ))
                    )),
                    hasSibling(allOf(
                            withId(R.id.incid_see_importancia_block),
                            hasDescendant(allOf(
                                    withId(R.id.incid_importancia_comunidad_view),
                                    withText(mActivity.getResources()
                                            .getStringArray(R.array.IncidImportanciaArray)[Math.round(incidImportancia.getImportancia())]))
                            ))),
                    hasSibling(allOf(
                            withText(incidencia.getDescripcion()),
                            withId(R.id.incid_descripcion_view)
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
    public void testCheckNotification_2() throws IOException, InterruptedException, GcmException
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            endPointImp.sendGcmMulticastRequestImp(getGcmMultiRequest(incidencia_closed_type));
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

            // Lista de incidencias cerradas.
            onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());
            // Verificamos que se muestran los datos de búsqueda de comunidad.
            onView(withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        }
    }
}
