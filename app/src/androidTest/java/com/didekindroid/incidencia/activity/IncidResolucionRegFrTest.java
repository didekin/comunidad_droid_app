package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkNavigateUp;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanOptions;
import static com.didekindroid.common.testutils.ActivityTestUtils.signUpAndUpdateTk;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.doIncidencia;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/02/16
 * Time: 15:14
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionRegFrTest {

    IncidImportancia incidJuanReal1;
    IncidResolucionRegEditSeeAc mActivity;

    @Rule
    public IntentsTestRule<IncidResolucionRegEditSeeAc> intentRule = new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            super.beforeActivityLaunched();
        }

        /**
         * Preconditions:
         * 1. A user WITH powers to resolve an incidencia is received.
         * 2. NO resolucion en BD.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
                UsuarioComunidad juanReal = ServOne.seeUserComusByUser().get(0);
                incidJuanReal1 = new IncidImportancia.IncidImportanciaBuilder(
                        doIncidencia(juanReal.getUsuario().getUserName(), "Incidencia Real One", juanReal.getComunidad().getC_Id(), (short) 43))
                        .usuarioComunidad(juanReal)
                        .importancia((short) 3).build();
                IncidenciaServ.regIncidImportancia(incidJuanReal1);
                Incidencia incidenciaDb = IncidenciaServ.seeIncidsOpenByComu(juanReal.getComunidad().getC_Id()).get(0).getIncidencia();
                incidJuanReal1 = IncidenciaServ.seeIncidImportancia(incidenciaDb.getIncidenciaId()).getIncidImportancia();
            } catch (UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanReal1);
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
        Thread.sleep(3000);
        mActivity = intentRule.getActivity();
        IncidImportancia incidImportancia = (IncidImportancia) mActivity.getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        // Preconditions: a user with powers to erase and modify is received.
        assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        // NO resolución en BD.
        assertThat(IncidenciaServ.seeResolucion(incidImportancia.getIncidencia().getIncidenciaId()), nullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        onView(withId(R.id.incid_resolucion_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_frg_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_desc_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed()));

        checkNavigateUp();
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // DatePicker tests.
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed())).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).inRoot(isDialog()).check(matches(isDisplayed()));
        // We pick a date.
        onView(withClassName(is(DatePicker.class.getName()))).perform(setDate(2016, 3, 21));
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("21/3/2016")
            )).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testOnEdit_1()
    {
        // Descripción errónea y fecha sin fijar.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_fecha_prev_msg, R.string.incid_resolucion_descrip_msg);
    }

    @Test
    public void testOnEdit_2()
    {
        // Descripción errónea.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        setFecha(setDate(2016, 3, 21));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_descrip_msg);
    }

    @Test
    public void testOnEdit_3()
    {
        // Coste erróneo.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg);
    }

    @Test
    public void testOnEdit_4()
    {
        // Coste y descripción erróneos.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg,
                R.string.incid_resolucion_descrip_msg);
    }

    @Test
    public void testOnEdit_5()
    {
        // Fecha inferior a fecha_alta incidencia.
        Calendar wrongDate = new GregorianCalendar();
        wrongDate.add(YEAR, -1);
        assertThat(wrongDate.getTimeInMillis() < incidJuanReal1.getFechaAlta().getTime(), is(true));
        setFecha(setDate(wrongDate.get(YEAR), wrongDate.get(MONTH), wrongDate.get(DAY_OF_MONTH)));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_fecha_prev_msg,
                R.string.incid_resolucion_descrip_msg);
    }

    @Test
    public void testOnRegister_1()
    {
        //Caso: OK

        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("1234,5"));
        Calendar today = new GregorianCalendar();
        today.add(YEAR, 1);
        setFecha(setDate(today.get(YEAR), today.get(MONTH), today.get(DAY_OF_MONTH)));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_frg)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.extra, incidJuanReal1));
    }

//    ============================= HELPER METHODS ===========================

    private void setFecha(ViewAction viewAction)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).perform(viewAction);
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
    }
}