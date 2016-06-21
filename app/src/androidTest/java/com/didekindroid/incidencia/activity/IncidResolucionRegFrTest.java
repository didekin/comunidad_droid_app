package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.testutils.IncidenciaTestUtils;
import com.didekindroid.usuario.testutils.CleanUserEnum;

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
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
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
                } catch (UiException | IOException e) {
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
    CleanUserEnum whatToClean()
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
        // We pick a date.
        onView(withClassName(is(DatePicker.class.getName()))).perform(setDate(2016, 3, 21));
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("21/3/2016")
            )).check(matches(isDisplayed()));
        }
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText("21 mar. 2016")
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
    public void testOnEdit_3() throws InterruptedException
    {
        // Coste erróneo.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        Thread.sleep(1500);
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.incid_resolucion_coste_prev_msg);
    }

    @Test
    public void testOnEdit_4() throws InterruptedException
    {
        // Coste y descripción erróneos.
        setFecha(setDate(2016, 3, 22));
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("Desc * no válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("novalid"));

        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        Thread.sleep(1500);
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
        assertThat(wrongDate.getTimeInMillis() < incidImportancia.getFechaAlta().getTime(), is(true));
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
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
    }

//    ============================= HELPER METHODS ===========================

    private void setFecha(ViewAction viewAction)
    {
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).perform(viewAction);
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
    }
}