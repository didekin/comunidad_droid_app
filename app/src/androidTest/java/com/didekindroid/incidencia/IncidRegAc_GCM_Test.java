package com.didekindroid.incidencia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.usuario.Usuario;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.incidencia.AppFBService.IncidTypeMsgHandler.INCIDENCIA_OPEN;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 *
 * Test GCM para el registro de una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidRegAc_GCM_Test extends Incidencia_GCM_Test {

    Usuario pepe;
    UsuarioComunidad pepeUserComu;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    /**
     * Test para RegGcmIntentService methods.
     */
    @Test
    public void testRegistrationGcmToken() throws Exception
    {
        // Preconditions for the test.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        checkToken();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testReceiveNotification() throws Exception
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        // Preconditions for the test: TOKEN en BD.
        assertThat(usuarioDaoRemote.getGcmToken(), is(FirebaseInstanceId.getInstance().getToken()));

        IncidImportancia incidPepe =
                new IncidImportancia.IncidImportanciaBuilder(doIncidencia(pepe.getUserName(), "Incidencia One", pepeUserComu.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeUserComu)
                        .importancia((short) 3)
                        .build();

        assertThat(IncidenciaServ.regIncidImportancia(incidPepe), is(2));
        checkNotification(INCIDENCIA_OPEN.getContentTextRsc());
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidRegAc>(IncidRegAc.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                Context context = creator.get().getContext();
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