package com.didekindroid.incidencia.resolucion;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.util.UIutils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.reSetDatePicker;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.util.UIutils.formatTimeToString;
import static com.didekindroid.util.UIutils.isCalendarPreviousTimeStamp;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 13/02/16
 * Time: 15:14
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionRegFrTest extends IncidResolucionAbstractTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(3000);
    }

    @Override
    IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {
            /**
             * Preconditions:
             * 1. A user WITH powers 'adm' in session.
             * 2. Resolucion intent == null.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
                } catch (IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
                return intent;
            }
        };
    }

    @Override
    UsuarioDataTestUtils.CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN;
    }

    //  ===============================  TESTS ================================
    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionRegFr();
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // DatePicker tests.
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed())).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).inRoot(isDialog()).check(matches(isDisplayed()));
        // Seleccionamos fecha de resolución: fecha de hoy, sin añadir ningún mes adicional.
        Calendar fechaPrev = reSetDatePicker(0, 0);
        closeDatePicker(activity);

        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(fechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(fechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
    }

    @Test
    public void test_registerResolucion_1() throws InterruptedException
    {
        // NOT OK: Descripción errónea y fecha sin fijar.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_fecha_prev_msg, R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void test_registerResolucion_2() throws InterruptedException
    {
        assertThat(isCalendarPreviousTimeStamp(setFechaEnPicker(0, 1), incidImportancia.getFechaAlta()), is(false));

        // NOT OK: Descripción errónea.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void test_registerResolucion_3() throws InterruptedException
    {
        assertThat(isCalendarPreviousTimeStamp(setFechaEnPicker(0, 1), incidImportancia.getFechaAlta()), is(false));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));

        // NOT OK: Coste erróneo.
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_coste_prev_msg);

        Thread.sleep(2000);
    }


    @Test
    public void test_registerResolucion_4() throws InterruptedException
    {
        assertThat(isCalendarPreviousTimeStamp(setFechaEnPicker(0, 1), incidImportancia.getFechaAlta()), is(false));
        // NOT OK: Coste y descripción erróneos.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_coste_prev_msg,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void test_registerResolucion_5() throws InterruptedException
    {
        // NOT OK: Fecha inferior a fecha_alta incidencia. Descripción ausente.
        Calendar fechaPrev = setFechaEnPicker(0, -1);
        assertThat(isCalendarPreviousTimeStamp(fechaPrev, incidImportancia.getFechaAlta()), is(true));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_fecha_prev_msg,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);

        // Intentamos corregir:
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        fechaPrev = setFechaEnPicker(0, 0);

        assertThat(isCalendarPreviousTimeStamp(fechaPrev, incidImportancia.getFechaAlta()), is(false));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());

        checkRegResolucionOk();
    }

    @Test
    public void test_registerResolucion_6() throws InterruptedException
    {
        // OK: fecha de hoy.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        Calendar fechaPrev = setFechaEnPicker(0, 0);
        assertThat(isCalendarPreviousTimeStamp(fechaPrev, incidImportancia.getFechaAlta()), is(false));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());

        checkRegResolucionOk();
    }

    @Test
    public void test_registerResolucion_7()
    {
        //Caso: OK

        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("1234,5"));
        setFechaEnPicker(0, 2);
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());

        checkRegResolucionOk();

        // Test de error resolución duplicada.
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.resolucion_duplicada, activity));
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
    }

//    ============================= HELPER METHODS ===========================

    private Calendar setFechaEnPicker(long fechaInicial, int monthsToAdd)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar fechaPrev = reSetDatePicker(fechaInicial, monthsToAdd);
        closeDatePicker(activity);
        return fechaPrev;
    }

    private void checkRegResolucionOk()
    {
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));

        checkUp();
        checkScreenResolucionRegFr();
    }
}