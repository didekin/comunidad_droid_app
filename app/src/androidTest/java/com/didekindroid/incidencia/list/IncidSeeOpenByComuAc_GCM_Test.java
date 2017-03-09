package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.Incidencia_GCM_Test;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 *
 * Test de integración GCM para la consulta de incidencias abiertas.
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenByComuAc_GCM_Test extends Incidencia_GCM_Test {

    Usuario pepe;

    @Test
    public void testUpdateGcmToken() throws Exception
    {
        // We check that the activity has sent the Firebase token to BD.
        checkToken();
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                try {
                    pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    // We'll test that the gcmToken is not updated in server.
                    assertThat(usuarioDao.getGcmToken(), nullValue());
                } catch (IOException | UiException e) {
                    fail();
                }
            }
        };
    }
}