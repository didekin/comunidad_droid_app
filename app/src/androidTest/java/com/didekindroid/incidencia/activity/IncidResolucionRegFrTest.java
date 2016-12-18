package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekindroid.R;
import com.didekindroid.exception.UiAppException;
import com.didekinaar.utils.UIutils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.closeDatePicker;
import static com.didekinaar.testutil.AarActivityTestUtils.reSetDatePicker;
import static com.didekinaar.utils.UIutils.formatTimeToString;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
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
                } catch (UiAppException | IOException | UiException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
                intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
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
    AarActivityTestUtils.CleanUserEnum whatToClean()
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
        Calendar fechaPrev = reSetDatePicker(0,0);
        closeDatePicker(mActivity);

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
    public void testOnEdit_1() throws InterruptedException
    {
        // NOT OK: Descripción errónea y fecha sin fijar.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_fecha_prev_msg, R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void testOnEdit_2() throws InterruptedException
    {
        setFechaEnPicker(0,1);

        // NOT OK: Descripción errónea.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void testOnEdit_3() throws InterruptedException
    {
        setFechaEnPicker(0,1);
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));

        // NOT OK: Coste erróneo.
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg);

        Thread.sleep(2000);
    }



    @Test
    public void testOnEdit_4() throws InterruptedException
    {
        setFechaEnPicker(0,1);
        // NOT OK: Coste y descripción erróneos.
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void testOnEdit_5() throws InterruptedException
    {
        // NOT OK: Fecha inferior a fecha_alta incidencia. Descripción ausente.
        Calendar fechaPrev = setFechaEnPicker(0,-1);
        assertThat(fechaPrev.getTimeInMillis() < incidImportancia.getFechaAlta().getTime(), is(true));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_fecha_prev_msg,
                R.string.incid_resolucion_descrip_msg);

        Thread.sleep(2000);
    }

    @Test
    public void testOnRegister_1()
    {
        //Caso: OK

        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("1234,5"));
        Calendar today = new GregorianCalendar();
        setFechaEnPicker(0,2);

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));

        checkUp();
        checkScreenResolucionRegFr();
    }

//    ============================= HELPER METHODS ===========================

    private Calendar setFechaEnPicker(long fechaInicial, int monthsToAdd)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar fechaPrev = reSetDatePicker(fechaInicial, monthsToAdd);
        closeDatePicker(mActivity);
        return fechaPrev;
    }
}