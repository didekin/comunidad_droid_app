package com.didekindroid.incidencia.core.reg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.incidencia.core.Incidencia_GCM_test_abs;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidencia;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test de integración GCM para el registro de una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegAc_GCM_Test extends Incidencia_GCM_test_abs {

    private Usuario pepe;
    private UsuarioComunidad pepeUserComu;

    @Test
    @TargetApi(Build.VERSION_CODES.M)
    public void testReceiveNotification()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }

        IncidImportancia incidPepe =
                new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeUserComu)
                        .importancia((short) 3)
                        .build();
        assertThat(incidenciaDao.regIncidImportancia(incidPepe), is(2));

        // We check that the notification is received.
        checkNotification(INCIDENCIA_OPEN.getBarNotificationId());
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                try {
                    pepe = regComuUserUserComuGetUser(COMU_ESCORIAL_PEPE);
                } catch (Exception e) {
                    fail();
                }
                pepeUserComu = userComuDao.seeUserComusByUser().blockingGet().get(0);
            }
        };
    }
}