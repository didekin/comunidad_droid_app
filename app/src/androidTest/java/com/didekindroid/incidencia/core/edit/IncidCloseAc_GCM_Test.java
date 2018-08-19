package com.didekindroid.incidencia.core.edit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.incidencia.core.Incidencia_GCM_test_abs;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionEditAc;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_CLOSE;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAdvances;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test de integración GCM para cierre de una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidCloseAc_GCM_Test extends Incidencia_GCM_test_abs {

    private Resolucion resolucion;

    @Test
    @TargetApi(Build.VERSION_CODES.M)
    public void testReceiveNotification()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));
        // We checkMenu that the notification is received.
        checkNotification(INCIDENCIA_CLOSE.getBarNotificationId());
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
                IncidImportancia incidImportancia = null;
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_PEPE);
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                } catch (Exception e) {
                    fail();
                }
                return new Intent()
                        .putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia)
                        .putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
            }
        };
    }
}