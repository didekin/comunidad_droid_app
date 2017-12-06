package com.didekindroid.incidencia.core.resolucion;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.edit.IncidEditAc;
import com.didekindroid.util.UIutils;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
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
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionRegFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incideEditMaxPowerFrLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtils.reSetDatePicker;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.util.UIutils.formatTimeToString;
import static com.didekindroid.util.UIutils.isCalendarPreviousTimeStamp;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 13/02/16
 * Time: 15:14
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionRegAcTest {

    IncidResolucionRegAc activity;
    IncidImportancia incidImportancia;

    @Rule
    public IntentsTestRule<IncidResolucionRegAc> testRule = new IntentsTestRule<IncidResolucionRegAc>(IncidResolucionRegAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                // A user WITH powers 'adm'.
                incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
            } catch (IOException | UiException e) {
                fail();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intentStack = new Intent(getTargetContext(), IncidEditAc.class);
                intentStack.putExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, true));
                create(getTargetContext()).addNextIntentWithParentStack(intentStack).startActivities();
            }

            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            return intent;
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = testRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

    //  ===============================  TESTS ================================
    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionRegFr();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(incidEditAcLayout);
        }
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

        sleep(2000);
    }

    @Test
    public void test_registerResolucion_2() throws InterruptedException
    {
        // NOT OK: Fecha inferior a fecha_alta incidencia. Descripción ausente. Coste erróneo.
        Calendar fechaPrev = setFechaEnPicker(0, -1);
        assertThat(isCalendarPreviousTimeStamp(fechaPrev, incidImportancia.getFechaAlta()), is(true));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_fecha_prev_msg,
                R.string.incid_resolucion_descrip_msg,
                R.string.incid_resolucion_coste_prev_msg);

        sleep(2000);

        // Intentamos corregir: fecha de hoy.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        fechaPrev = setFechaEnPicker(0, 0);
        assertThat(isCalendarPreviousTimeStamp(fechaPrev, incidImportancia.getFechaAlta()), is(false));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        // Check coste erróneo.
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.incid_resolucion_coste_prev_msg);

        sleep(2000);
    }

    @Test
    public void test_registerResolucion_3()
    {
        /*Caso: OK*/
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("1234,5"));
        setFechaEnPicker(0, 2);
        // Run
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        // Check.
        checkRegResolucionOk();
    }

//    ============================= HELPER METHODS ===========================

    @SuppressWarnings("SameParameterValue")
    private Calendar setFechaEnPicker(long fechaInicial, int monthsToAdd)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar fechaPrev = reSetDatePicker(fechaInicial, monthsToAdd);
        closeDatePicker(activity);
        return fechaPrev;
    }

    private void checkRegResolucionOk()
    {
        onView(withId(incidEditAcLayout)).check(matches(isDisplayed()));
        onView(withId(incideEditMaxPowerFrLayout)).check(matches(isDisplayed()));
        // hasResolucion == true, because it has been registered.
        intended(hasExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, true)));
    }
}