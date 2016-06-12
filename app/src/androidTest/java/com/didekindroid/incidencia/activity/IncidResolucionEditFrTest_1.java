package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekin.incidservice.dominio.Resolucion;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.usuario.testutils.CleanUserEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
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
import static com.didekindroid.common.utils.UIutils.SPAIN_LOCALE;
import static com.didekindroid.common.utils.UIutils.formatTimeToString;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.testutils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_PLAZUELA5_JUAN;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionEditFrTest_1 extends IncidResolucionAbstractTest {

    @Override
    IntentsTestRule<IncidResolucionRegEditSeeAc> doIntentRule()
    {
        return new IntentsTestRule<IncidResolucionRegEditSeeAc>(IncidResolucionRegEditSeeAc.class) {
            /**
             * Preconditions:
             * 1. A user WITH powers 'adm' in sesssion.
             * 2. A resolucion in BD and intent.
             * 3. Resolucion WITHOUT avances.
             * */
            @Override
            protected Intent getActivityIntent()
            {
                try {
                    incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
                    Thread.sleep(1000);
                    resolucion = insertGetResolucionNoAdvances(incidImportancia);

                } catch (UiException | InterruptedException | IOException e) {
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

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionEditFr();
    }

    @Test
    public void testOnData_1()
    {
        checkDataResolucionEditFr();
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
        assertThat(Math.abs(resolucionDb.getFechaPrev().getTime() - resolucion.getFechaPrev().getTime()) < 1000, is(true));
    }

    @Test
    public void testOnEdit_2() throws UiException
    {
        // Caso: cambiamos la fecha prevista.

        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar newCalendar = new GregorianCalendar();
        newCalendar.setTimeInMillis(resolucion.getFechaPrev().getTime());
        newCalendar.add(MONTH, 1); // Aumentamos un mes la fecha estimada.
        // Android PickerActions substract 1 from the month passed to setDate(), so we increased the month parameter value in 1 before passing it.
        onView(withClassName(is(DatePicker.class.getName())))
                .perform(setDate(newCalendar.get(Calendar.YEAR), newCalendar.get(MONTH) + 1, newCalendar.get(DAY_OF_MONTH)));
        onView(withText(mActivity.getString(android.R.string.ok))).perform(click());
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(newCalendar.getTimeInMillis()))
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
    public void testCloseIncidenciaAndBack()
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
    public void testCloseIncidenciaAndUp()
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
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
    }
}
