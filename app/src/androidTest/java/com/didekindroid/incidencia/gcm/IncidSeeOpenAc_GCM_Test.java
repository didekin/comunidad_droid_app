package com.didekindroid.incidencia.gcm;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekindroid.incidencia.activity.IncidRegAc;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import static com.didekinaar.testutil.AarActivityTestUtils.signUpAndUpdateTk;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekinaar.utils.UIutils.updateIsGcmTokenSentServer;
import static com.didekinaar.usuario.AarUsuarioService.AarUserServ;
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
                    pepeUserComu = AarUserComuServ.seeUserComusByUser().get(0);
                    // Pepe hasn't got a gcmToken.
                    Objects.equals(AarUserServ.getGcmToken() == null, true);
                } catch (IOException | UiAarException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}