package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.incidencia.core.Incidencia_GCM_test_abs;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 * <p>
 * Test de integración GCM para la consulta de incidencias abiertas.
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAc_Open_GCM_Test extends Incidencia_GCM_test_abs {

    @Test
    public void testUpdateGcmToken()
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
                regUserComuWithTkCache(COMU_ESCORIAL_PEPE);
                // We'll test that the gcmToken is not updated in server.
                assertThat(usuarioDaoRemote.getGcmToken(), nullValue());
                return new Intent().putExtra(INCID_CLOSED_LIST_FLAG.key, false);
            }
        };
    }
}