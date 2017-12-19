package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.Incidencia_GCM_abs_Test;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test de integración GCM para la consulta de incidencias abiertas.
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAc_Open_GCM_Test extends Incidencia_GCM_abs_Test {

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
        return new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {

            @Override
            protected Intent getActivityIntent()
            {
                try {
                    pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    // We'll test that the gcmToken is not updated in server.
                    assertThat(usuarioDaoRemote.getGcmToken(), nullValue());
                } catch (IOException | UiException e) {
                    fail();
                }
                return new Intent().putExtra(INCID_CLOSED_LIST_FLAG.key, false);
            }
        };
    }
}