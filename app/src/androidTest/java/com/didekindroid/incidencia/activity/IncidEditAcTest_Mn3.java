package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
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
import static com.didekin.common.oauth2.Rol.PRESIDENTE;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.incidencia.testutils.IncidenciaMenuTestUtils.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/01/16
 * Time: 16:57
 */

/**
 * Tests sobre una incidencia CON resolución en BD.
 * El usuario NO tiene autoridad 'adm'.
 */
@RunWith(AndroidJUnit4.class)
public class IncidEditAcTest_Mn3 {

    IncidEditAc mActivity;
    UsuarioComunidad juanEscorial;
    IncidImportancia incidJuanEscorial;
    Resolucion resolucion;

    @Rule
    public IntentsTestRule<IncidEditAc> intentRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {

            super.beforeActivityLaunched();
        }

        /**
         * The users has authority to erase and modify.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_JUAN);
                juanEscorial = ServOne.seeUserComusByUser().get(0);
                incidJuanEscorial = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanEscorial.getUsuario().getUserName(), "Incidencia Escorial", juanEscorial.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanEscorial)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidJuanEscorial);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(juanEscorial.getComunidad().getC_Id()).get(0);
                incidJuanEscorial = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId());
                Thread.sleep(1000);

                // Necesitamos usuario con 'adm' para registrar resolución.
                assertThat(ServOne.regUserAndUserComu(new UsuarioComunidad.UserComuBuilder(juanEscorial.getComunidad(), USER_PEPE).roles(PRESIDENTE.function).build()), is(true));
                updateSecurityData(USER_PEPE.getUserName(), USER_PEPE.getPassword());
                resolucion = doResolucion(incidJuanEscorial.getIncidencia(), "resol_desc1", 1000, new Timestamp(new Date().getTime()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));

                // Volvemos a usuario del test.
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanEscorial);
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
        IncidImportancia incidImportancia = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        // Preconditions: a user without powers to erase and modify is received.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(false));
        // Resolución en BD.
        assertThat(IncidenciaServ.seeResolucion(incidImportancia.getIncidencia().getIncidenciaId()), is(resolucion));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

//    ============================  TESTS  ===================================

    @Test
    public void testIncidResolucionReg_Mn() throws Exception
    {
        INCID_RESOLUCION_REG_EDIT_AC.checkMenuItem_WTk(mActivity);
        onView(ViewMatchers.withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));
        // Extra con incidImportancia.
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanEscorial));
        // Extra con resolución.
        intended(hasExtra(INCID_RESOLUCION_OBJECT.extra, resolucion));
    }
}