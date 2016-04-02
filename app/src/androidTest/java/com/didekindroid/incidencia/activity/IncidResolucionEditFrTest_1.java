package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.UIutils;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doResolucion;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_ESCORIAL_PEPE;
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
public class IncidResolucionEditFrTest_1 {

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
         * 3. Resolucion WITHOUT avances.
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
                Thread.sleep(1000);

                // Registramos resolución.
                resolucion = doResolucion(incidPepeEscorial.getIncidencia(), "desc_resolucion", 1000, new Timestamp(new GregorianCalendar(2016, 3, 25).getTimeInMillis()));
                assertThat(IncidenciaServ.regResolucion(resolucion), is(1));
                resolucion = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
            } catch (UiException | InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidPepeEscorial);
            intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
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
        // Precondition: resolución in BD and intent.
        Resolucion resolucionIntent = (Resolucion) mActivity.getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key);
        assertThat(resolucionIntent, is(resolucion));
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
        onView(withId(android.R.id.list)).check(matches(not(isDisplayed())));
        onView(withId(android.R.id.empty)).check(matches(isDisplayed()));

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
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));
    }

    @Test
    public void testOnEdit_1() throws UiException
    {
        // Caso: no cambiamos nada y pulsamos modificar.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk();
        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.equals(resolucion), is(true));
        assertThat(resolucionDb.getFechaPrev().equals(resolucion.getFechaPrev()), is(true));
    }

    @Test
    public void testOnEdit_2() throws UiException
    {
        // Caso: cambiamos la fecha prevista.

        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        // We pick a date.
        onView(withClassName(is(DatePicker.class.getName()))).perform(setDate(2016, 4, 30));
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("30/4/2016")
            )).check(matches(isDisplayed()));
        }
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk();
    }

    @Test
    public void testOnEdit_3() throws UiException
    {
        // Caso: añadimos un avance con descripción Ok y cambiamos coste (admite importes negativos).
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance_desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        checKOk();
        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(1));
        assertThat(resolucionDb.getAvances().get(0).getAvanceDesc(), is("avance_desc_válida"));
        assertThat(resolucionDb.getCosteEstimado(), is(-1234));
    }

    @Test
    public void testOnEdit_4() throws UiException
    {
        // Caso: descripción de avance errónea.
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance * no válido"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.incid_resolucion_avance_rot);
    }

    @Test
    public void testCloseIncidencia_1()
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());

        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));
        intended(not(hasExtraWithKey(INCID_IMPORTANCIA_OBJECT.key)));

        // Damos back e intentamos modificar la incidencia. Nos da error.
        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testCloseIncidencia_2()
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());

        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));
        intended(not(hasExtraWithKey(INCID_IMPORTANCIA_OBJECT.key)));
        // Up Navigate.
        checkNavigateUp();
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

//    ============================= HELPER METHODS ===========================

    private void checKOk()
    {
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_frg)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidPepeEscorial));
    }
}
