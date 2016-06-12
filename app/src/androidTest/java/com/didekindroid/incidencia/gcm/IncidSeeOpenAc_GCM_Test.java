package com.didekindroid.incidencia.gcm;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.activity.IncidRegAc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkState;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 */
@RunWith(AndroidJUnit4.class)
public class IncidSeeOpenAc_GCM_Test extends Incidencia_GCM_Test {

    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    /**
     * Test para GcmRegistrationIntentService methods.
     */
    @Test
    public void testRegistrationGcmToken() throws Exception
    {
        // Preconditions for the test.
        assertThat(checkPlayServices(mActivity), is(true));
        assertThat(isRegisteredUser(mActivity), is(true));

        checkToken();
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                Context context = InstrumentationRegistry.getTargetContext();
                updateIsGcmTokenSentServer(false, context);
                try {
                    pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    pepeUserComu = ServOne.seeUserComusByUser().get(0);
                    // Pepe hasn't got a gcmToken.
                    checkState(ServOne.getGcmToken() == null);
                } catch (UiException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}