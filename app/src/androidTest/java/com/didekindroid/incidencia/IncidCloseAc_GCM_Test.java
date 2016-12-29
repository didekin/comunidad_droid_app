package com.didekindroid.incidencia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekinaar.exception.UiException;
import com.didekindroid.incidencia.activity.IncidResolucionRegEditSeeAc;
import com.google.firebase.iid.FirebaseInstanceId;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekindroid.incidencia.AppFBService.IncidTypeMsgHandler.INCIDENCIA_CLOSE;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 27/11/15
 * Time: 16:38
 *
 * Test GCM para cierre de una incidencia.
 */
@RunWith(AndroidJUnit4.class)
public class IncidCloseAc_GCM_Test extends Incidencia_GCM_Test {

    Resolucion resolucion;

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

    @TargetApi(Build.VERSION_CODES.M)
    @Test
    public void testReceiveNotification() throws Exception
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        // Preconditions for the test: TOKEN en BD.
        assertThat(usuarioDaoRemote.getGcmToken(), is(FirebaseInstanceId.getInstance().getToken()));

        assertThat(IncidenciaServ.closeIncidencia(resolucion), is(2));
        checkNotification(INCIDENCIA_CLOSE.getContentTextRsc());
    }

//  ================================= Helper methods  ==========================================

    @Override
    protected IntentsTestRule<? extends Activity> doIntentsTestRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {

            @Override
            protected void beforeActivityLaunched()
            {
                Context context = InstrumentationRegistry.getTargetContext();
                TKhandler.updateIsGcmTokenSentServer(false);
            }

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
                    Thread.sleep(1000);
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);
                } catch ( InterruptedException | IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
                return intent;
            }
        };
    }
}