package com.didekindroid.incidencia.core;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION.SDK_INT;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_CLOSE;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.RESOLUCION_OPEN;
import static com.didekindroid.incidencia.testutils.GcmTestConstant.PACKAGE_TEST;
import static com.didekindroid.incidencia.testutils.IncidTestData.COSTE_ESTIM_DEFAULT;
import static com.didekindroid.incidencia.testutils.IncidTestData.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAvances;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static java.util.Calendar.SECOND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test de integración GCM para cierre de una incidencia.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class Incid_Firebase_Notif_Test {

    private NotificationManager notificationManager;

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().startActivitySync(new Intent(getTargetContext(), ActivityMock.class).setFlags(FLAG_ACTIVITY_NEW_TASK));
        notificationManager = (NotificationManager) getTargetContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @After
    public void tearDown() throws Exception
    {
        notificationManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testRegIncidencia() throws Exception
    {
        if (SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        // Preconditions.
        Usuario pepe = regComuUserUserComuGetUser(COMU_ESCORIAL_PEPE);
        UsuarioComunidad pepeUserComu = userComuDao.seeUserComusByUser().blockingGet().get(0);
        IncidImportancia incidencia =
                new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeUserComu)
                        .importancia((short) 3)
                        .build();
        notificationManager.cancelAll(); // Erase notifications produced for the previous lines calls.
        // Exec.
        assertThat(incidenciaDao.regIncidImportancia(incidencia).blockingGet(), is(2));
        checkNotification(INCIDENCIA_OPEN.getBarNotificationId());
    }

    @Test
    public void testRegResolucion() throws Exception
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // Preconditions.
        Resolucion resolucion = doResolucion(insertGetIncidImportancia(COMU_PLAZUELA5_PEPE).getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 30)));
        notificationManager.cancelAll(); // Erase notifications produced for the previous line call.
        // Exec.
        assertThat(incidenciaDao.regResolucion(resolucion).blockingGet(), is(1));
        checkNotification(RESOLUCION_OPEN.getBarNotificationId());
    }

    @Test
    public void testCloseIncidencia() throws Exception
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        // Preconditions.
        Resolucion resolucion = insertGetResolucionNoAvances(insertGetIncidImportancia(COMU_PLAZUELA5_PEPE));
        notificationManager.cancelAll(); // Erase notifications produced for the previous line call.
        // Exec.
        assertThat(incidenciaDao.closeIncidencia(resolucion).blockingGet(), is(2));
        checkNotification(INCIDENCIA_CLOSE.getBarNotificationId());
    }

    // ========================  Helpers =========================

    private void checkNotification(int notificationId)
    {
        // We check that the notification is received.
        waitAtMost(16, SECONDS).until(() -> notificationManager.getActiveNotifications().length, is(1));

        StatusBarNotification barNotification = notificationManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(notificationId));
        assertThat(barNotification.getNotification().contentIntent.getCreatorPackage(), is(PACKAGE_TEST));
    }
}