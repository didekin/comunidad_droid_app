package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests sobre menús, CON un userComu administrador. Resolución en BD != null.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn4 {

    IncidEditAc mActivity;
    UsuarioComunidad juanPlazuelas;
    IncidImportancia incidJuanPlazuelas;
    Resolucion resolucion;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {

            super.beforeActivityLaunched();
        }

        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                juanPlazuelas = ServOne.seeUserComusByUser().get(0);
                incidJuanPlazuelas = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanPlazuelas.getUsuario().getUserName(), "Incidencia Plazueles One", juanPlazuelas.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanPlazuelas)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidJuanPlazuelas);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(juanPlazuelas.getComunidad().getC_Id()).get(0);
                incidJuanPlazuelas = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();
                Thread.sleep(1000);
                // Preconditions: resolución en BD.
                resolucion = doResolucion(incidJuanPlazuelas.getIncidencia(), "resol_desc1", 1000, new Timestamp(new Date().getTime()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidJuanPlazuelas);
            return intent;
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
        IncidImportancia incidImportancia = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        // Preconditions: a user with powers to erase and modify is received.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        // Resolución en BD.
        assertThat(IncidenciaServ.seeResolucion(incidImportancia.getIncidencia().getIncidenciaId()),is(resolucion));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem_WTk(mActivity);
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidJuanPlazuelas));
        // Extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.key, resolucion));
    }
}