package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidservice.dominio.Avance;
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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.FragmentTags.incid_resolucion_edit_fr_tag;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
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
public class IncidResolucionEditFrTest_2 {

    IncidResolucionRegEditSeeAc mActivity;
    UsuarioComunidad pepeEscorial;
    IncidImportancia incidPepeEscorial;
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
         * 1. A user WITH powers to edit a resolucion is received.
         * 2. A resolucion in BD and intent.
         * 3. Resolucion with avances.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeEscorial = ServOne.seeUserComusByUser().get(0);
                incidPepeEscorial = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(pepeEscorial.getUsuario().getUserName(), "Incidencia Escorial", pepeEscorial.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(pepeEscorial)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidPepeEscorial);
                IncidenciaUser incidenciaUserDb = IncidenciaServ.seeIncidsOpenByComu(pepeEscorial.getComunidad().getC_Id()).get(0);
                incidPepeEscorial = IncidenciaServ.seeIncidImportancia(incidenciaUserDb.getIncidencia().getIncidenciaId()).getIncidImportancia();

                // Registramos resolución.
                Thread.sleep(1000);
                resolucion = doResolucion(incidPepeEscorial.getIncidencia(), "desc_resolucion", 1000, new Timestamp(new GregorianCalendar(2016, 3, 25).getTimeInMillis()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());

                // Modificamos resolución.
                Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").userName(USER_PEPE.getUserName()).build();
                List<Avance> avances = new ArrayList<>(1);
                avances.add(avance);
                resolucion = new Resolucion.ResolucionBuilder(incidPepeEscorial.getIncidencia())
                        .copyResolucion(resolucion)
                        .avances(avances)
                        .build();
                assertThat(IncidenciaServ.modifyResolucion(resolucion), is(2));
                resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidPepeEscorial);
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
        IncidResolucionEditFr editFr = (IncidResolucionEditFr) mActivity.getFragmentManager().findFragmentByTag(incid_resolucion_edit_fr_tag);
        assertThat(editFr, notNullValue());
        IncidImportancia incidImportancia = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        // Preconditions: a user with powers to erase and modify is received.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        // Precondition: resolución in BD and intent.
        Resolucion resolucionIntent = (Resolucion) mActivity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.extra);
        assertThat(resolucionIntent, is(resolucion));
        assertThat(resolucionIntent.getAvances().size(), is(1));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_avance_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_txt)).check(matches(isDisplayed()));
        // Lista vacía.
        onView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(withId(android.R.id.empty)).check(matches(not(isDisplayed())));

        checkNavigateUp();
    }

    @Test
    public void testOnData_1()
    {
        // Caso: los datos que se muestran por defecto.
        // Fecha.
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("25/4/2016")
            )).check(matches(isDisplayed()));
        }
        // Coste.
        onView(allOf(
                withId(R.id.incid_resolucion_coste_prev_ed),
                withText("1.000")
        )).check(matches(isDisplayed()));
        // Resolución.
        onView(allOf(
                withId(R.id.incid_resolucion_txt),
                withText("desc_resolucion")
        )).check(matches(isDisplayed()));
        // Avances.
        Avance avance = resolucion.getAvances().get(0);
        onData(is(avance)).inAdapterView(withId(android.R.id.list)).check(matches(isDisplayed()));
        onView(allOf(
                withText("avance1_desc"),
                withId(R.id.incid_avance_desc_view)
        )).check(matches(isDisplayed()));
        onView(allOf(
                withText(formatTimeStampToString(avance.getFechaAlta())),
                withId(R.id.incid_avance_fecha_view),
                hasSibling(allOf(
                        withId(R.id.incid_avance_aliasUser_view),
                        withText(USER_PEPE.getUserName())
                )))).check(matches(isDisplayed()));
    }

    @Test
    public void testOnEdit_1() throws UiException
    {
        // Caso: añadimos un avance con descripción Ok .
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance2_desc_válida"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_frg)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.extra, incidPepeEscorial));

        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(2));
        assertThat(resolucionDb.getAvances().get(1).getAvanceDesc(), is("avance2_desc_válida"));
    }

/*    ============================= HELPER METHODS ===========================*/
}
