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
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.common.oauth2.Rol.PRESIDENTE;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.testutils.ActivityTestUtils.updateSecurityData;
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN_AND_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionSeeFrTest_2 {

    IncidResolucionRegEditSeeAc mActivity;
    UsuarioComunidad juanEscorial;
    IncidImportancia incidJuanEscorial;
    Resolucion resolucion;

    @Rule
    public IntentsTestRule<IncidResolucionRegEditSeeAc> intentRule = new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. A user WITHOUT powers to edit a resolucion is received.
         * 2. A resolucion in BD and intent.
         * 3. Resolucion with avances.
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
                // Registramos resolución.
                resolucion = doResolucion(incidJuanEscorial.getIncidencia(), "resol_desc1", 1000, new Timestamp(new GregorianCalendar(2016, 3, 25).getTimeInMillis()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
                // Volvemos a usuario del test.
                updateSecurityData(USER_JUAN.getUserName(), USER_JUAN.getPassword());
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanEscorial);
            intent.putExtra(INCID_RESOLUCION_OBJECT.extra, resolucion);
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
        // Precondition: resolución in BD and intent.
        Resolucion resolucionIntent = (Resolucion) mActivity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.extra);
        assertThat(resolucionIntent, is(resolucion));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN_AND_PEPE);
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_see_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));
        // Lista vacía.
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

        checkNavigateUp();
    }

    @Test
    public void testOnData_1()
    {
        // Fecha.
        if (Locale.getDefault().equals(SPAIN_LOCALE)){
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("25/4/2016")
            )).check(matches(isDisplayed()));
        }
        // Coste.
        onView(allOf(
                withId(R.id.incid_resolucion_coste_prev_view),
                withText("1.000")
        )).check(matches(isDisplayed()));
        // Resolución.
        onView(allOf(
                withId(R.id.incid_resolucion_txt),
                withText("resol_desc1")
        )).check(matches(isDisplayed()));
        // Avances.
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));
    }
}
