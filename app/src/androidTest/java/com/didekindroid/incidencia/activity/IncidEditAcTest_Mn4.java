package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.IncidAndResolBundle;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn4 extends IncidEditAbstractTest {

    Resolucion resolucion;

    @Override
    IntentsTestRule<IncidEditAc> doIntentRule()
    {
        return new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {

            @Override
            protected Intent getActivityIntent()
            {
                IncidAndResolBundle incidAndResolBundle = null;

                try {
                    signUpAndUpdateTk(COMU_PLAZUELA5_JUAN); // Usuario ADM.
                    juanUserComu = AppUserComuServ.seeUserComusByUser().get(0);
                    incidenciaJuan = new IncidImportancia.IncidImportanciaBuilder(
                            doIncidencia(juanUserComu.getUsuario().getUserName(), "Incidencia Plazueles One", juanUserComu.getComunidad().getC_Id(), (short) 43))
                            .usuarioComunidad(juanUserComu)
                            .importancia((short) 3).build();
                    IncidenciaServ.regIncidImportancia(incidenciaJuan);
                    IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(juanUserComu.getComunidad().getC_Id()).get(0);
                    incidenciaJuan = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
                    Thread.sleep(1000);
                    // Preconditions: resolución en BD.
                    resolucion = doResolucion(incidenciaJuan.getIncidencia(), "resol_desc1", 1000, new Timestamp(new Date().getTime()));
                    assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                    incidAndResolBundle = IncidenciaServ.seeIncidImportancia(resolucion.getIncidencia().getIncidenciaId());
                    incidenciaJuan = incidAndResolBundle.getIncidImportancia();
                } catch ( InterruptedException | IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan);
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle != null && incidAndResolBundle.hasResolucion());
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
    UsuarioDataTestUtils.CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN;
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem_WTk(mActivity);

        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidenciaJuan));
        // Extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion));

        checkUp();
        checkScreenEditMaxPowerFr();
    }
}