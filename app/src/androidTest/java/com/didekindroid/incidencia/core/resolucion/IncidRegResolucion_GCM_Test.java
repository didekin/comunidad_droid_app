package com.didekindroid.incidencia.core.resolucion;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.incidencia.core.Incidencia_GCM_test_abs;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.RESOLUCION_OPEN;
import static com.didekindroid.incidencia.testutils.IncidTestData.COSTE_ESTIM_DEFAULT;
import static com.didekindroid.incidencia.testutils.IncidTestData.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidTestData.doResolucion;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test GCM para el registro de la resolución de una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegResolucion_GCM_Test extends Incidencia_GCM_test_abs {

    private IncidImportancia incidImportancia;

    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testReceiveNotification()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        Resolucion resolucion = doResolucion(incidImportancia.getIncidencia(),
                RESOLUCION_DEFAULT_DESC,
                COSTE_ESTIM_DEFAULT,
                incidImportancia.getFechaAlta());
        assertThat(incidenciaDao.regResolucion(resolucion), is(1));

        // We check that the notification is received.
        checkNotification(RESOLUCION_OPEN.getBarNotificationId());
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidResolucionEditAc>(IncidResolucionEditAc.class) {
            /**
             * Preconditions:
             * 1. A user WITH powers 'adm' in sesssion.
             * 2. A resolucion in BD and intent.
             * 3. Resolucion WITHOUT avances.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_PEPE);
                } catch (Exception e) {
                    fail();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                return intent;
            }
        };
    }
}