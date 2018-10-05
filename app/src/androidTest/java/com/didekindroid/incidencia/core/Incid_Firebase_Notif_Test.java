package com.didekindroid.incidencia.core;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.getMilliSecondsFromCalendarAdd;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetUserComu;
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

    private static NotificationManager notificationManager;
    private static UsuarioComunidad pepeEscorial;

    @BeforeClass
    public static void setStatically() throws Exception
    {
        notificationManager = (NotificationManager) getTargetContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        pepeEscorial = signUpGetUserComu(COMU_ESCORIAL_PEPE);
    }

    @Before
    public void setUp() throws Exception
    {
        getInstrumentation().startActivitySync(new Intent(getTargetContext(), ActivityMock.class).setFlags(FLAG_ACTIVITY_NEW_TASK));
    }

    @AfterClass
    public static void tearDown()
    {
        notificationManager.cancelAll();
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void testUnique()
    {
        if (SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        // testRegIncidencia.
        IncidImportancia incidencia =
                new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(
                                pepeEscorial.getUsuario().getUserName(),
                                "Incidencia One",
                                pepeEscorial.getComunidad().getC_Id(),
                                (short) 43))
                        .usuarioComunidad(pepeEscorial)
                        .importancia((short) 3)
                        .build();
        assertThat(incidenciaDao.regIncidImportancia(incidencia).blockingGet(), is(2));
        checkNotification(INCIDENCIA_OPEN.getBarNotificationId());

        // testRegResolucion
        Incidencia incidenciaDb = incidenciaDao.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).blockingGet().get(0).getIncidencia();
        Resolucion resolucion = doResolucion(
                incidenciaDao.seeIncidImportanciaRaw(incidenciaDb.getIncidenciaId())
                        .blockingGet().getIncidImportancia().getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                new Timestamp(getMilliSecondsFromCalendarAdd(SECOND, 30))
        );
        assertThat(incidenciaDao.regResolucion(resolucion).blockingGet(), is(1));
        checkNotification(RESOLUCION_OPEN.getBarNotificationId());

        // testCloseIncidencia
        resolucion = incidenciaDao.seeResolucionRaw(resolucion.getIncidencia().getIncidenciaId()).blockingGet();
        assertThat(incidenciaDao.closeIncidencia(resolucion).blockingGet(), is(2));
        checkNotification(INCIDENCIA_CLOSE.getBarNotificationId());
    }

    // ========================  Helpers =========================

    private void checkNotification(int notificationId)
    {
        // We check that the notification is received.
        waitAtMost(20, SECONDS).until(() -> notificationManager.getActiveNotifications().length, is(1));

        StatusBarNotification barNotification = notificationManager.getActiveNotifications()[0];
        assertThat(barNotification.getId(), is(notificationId));
        assertThat(barNotification.getNotification().contentIntent.getCreatorPackage(), is(PACKAGE_TEST));

        notificationManager.cancelAll();
        waitAtMost(10, SECONDS).until(() -> notificationManager.getActiveNotifications().length, is(0));
    }
}