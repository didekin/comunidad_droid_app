package com.didekindroid.incidencia;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiException;
import com.didekindroid.incidencia.activity.IncidRegAc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;


import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
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
     * Test para AarFBRegIntentService methods.
     */
    @Test
    public void testRegistrationGcmToken() throws Exception
    {
        // Preconditions for the test.
        assertThat(TKhandler.isRegisteredUser(), is(true));

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
                TKhandler.updateIsGcmTokenSentServer(false);
                try {
                    pepe = signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    pepeUserComu = AppUserComuServ.seeUserComusByUser().get(0);
                    // Pepe hasn't got a gcmToken.
                    Objects.equals(usuarioDaoRemote.getGcmToken() == null, true);
                } catch (IOException | UiException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}