package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekin.incidencia.dominio.IncidAndResolBundle;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.exception.UiAarException;
import com.didekindroid.incidencia.exception.UiAppException;
import com.didekinaar.testutil.CleanUserEnum;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekinaar.testutil.AarActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.RESOLUCION_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:51
 */
@SuppressWarnings("ConstantConditions")
public class IncidEditAcMaxPowerTest_3 extends IncidEditAbstractTest {

    IncidAndResolBundle incidResolBundlePepe;

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            /**
             * Preconditions:
             * 1. An IncidenciaUser with powers to modify and to erase is received.
             * 2. There is resolucion in BD.
             * Postconditions:
             * 1. Erase button is NOT shown.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                    UsuarioComunidad pepeEscorial = AarUserComuServ.seeUserComusByUser().get(0);
                    IncidImportancia incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(pepeEscorial.getUsuario().getUserName(), INCID_DEFAULT_DESC, pepeEscorial.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(pepeEscorial)
                            .importancia((short) 3)
                            .build();
                    IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                    Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0).getIncidencia();
                    // Registramos resolución.
                    Thread.sleep(1000);
                    Resolucion resolucion = doResolucion(incidenciaDb, RESOLUCION_DEFAULT_DESC, 1000, new Timestamp(new Date().getTime()));
                    assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                    incidResolBundlePepe = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId());
                    incidenciaPepe = incidResolBundlePepe.getIncidImportancia();
                } catch (UiAppException | InterruptedException | IOException | UiAarException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaPepe);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidResolBundlePepe.hasResolucion());
                return intent;
            }
        };
    }

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Override
    CleanUserEnum whatToClean()
    {
        return CLEAN_PEPE;
    }

    @Test
    public void testOnCreate() throws Exception
    {
        checkScreenEditMaxPowerFr();
    }
}