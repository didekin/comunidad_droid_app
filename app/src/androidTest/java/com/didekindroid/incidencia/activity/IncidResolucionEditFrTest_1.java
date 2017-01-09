package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.incidencia.dominio.Resolucion;
import com.didekinaar.exception.UiException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;
import com.didekindroid.R;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkUp;
import static com.didekinaar.testutil.AarActivityTestUtils.reSetDatePicker;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekinaar.utils.UIutils.SPAIN_LOCALE;
import static com.didekinaar.utils.UIutils.formatTimeToString;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.IncidenciaTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.IncidenciaTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.usuariocomunidad.UserComuTestUtil.COMU_PLAZUELA5_JUAN;
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

                } catch ( InterruptedException | IOException | UiException e) {
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
    UsuarioDataTestUtils.CleanUserEnum whatToClean()
    {
        return CLEAN_JUAN;
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionEditFr();
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
        // Caso OK: no cambiamos nada y pulsamos modificar. Mantiene los datos de la resolución.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk();
        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.equals(resolucion), is(true));
        assertThat(Math.abs(resolucionDb.getFechaPrev().getTime() - resolucion.getFechaPrev().getTime()) < 1000, is(true));

        checkUp();
        checkScreenResolucionEditFr();
        checkDataResolucionEditFr();
    }

    @Test
    public void testOnEdit_2() throws UiException
    {
        // Caso OK: cambiamos la fecha prevista.

        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar newFechaPrev = reSetDatePicker(resolucion.getFechaPrev().getTime(), 1);

        AarActivityTestUtils.closeDatePicker(mActivity);

        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(newFechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk();

        checkUp();
        checkScreenResolucionEditFr();
    }

    @Test
    public void testOnEdit_3() throws UiException
    {
        // Caso OK: añadimos un avance con descripción Ok y cambiamos coste (admite importes negativos).
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance_desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        checKOk();
        Resolucion resolucionDb = IncidenciaServ.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(1));
        assertThat(resolucionDb.getAvances().get(0).getAvanceDesc(), is("avance_desc_válida"));
        assertThat(resolucionDb.getCosteEstimado(), is(-1234));

        checkUp();
        checkScreenResolucionEditFr();
    }

    @Test
    public void testOnEdit_4() throws UiException, InterruptedException
    {
        // Caso NO OK: descripción de avance errónea.
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance * no válido"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        checkToastInTest(R.string.error_validation_msg, mActivity, R.string.incid_resolucion_avance_rot);
        Thread.sleep(2000);
    }

    @Test
    public void testCloseIncidenciaAndBack() throws InterruptedException
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());

        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));
        intended(not(hasExtraWithKey(INCID_IMPORTANCIA_OBJECT.key)));

        // Damos back e intentamos modificar la incidencia. Nos da error.
        Thread.sleep(3000);
        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testCloseIncidenciaAndUp() throws InterruptedException
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());

        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));
        intended(not(hasExtraWithKey(INCID_IMPORTANCIA_OBJECT.key)));

        // Up Navigate.
        checkUp();
        checkScreenResolucionEditFr();
        checkDataResolucionEditFr();
        // Intentamos modificar la incidencia: error nos manda a la consulta.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checkToastInTest(R.string.incidencia_wrong_init, mActivity);
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
        Thread.sleep(2000);
    }

//    ============================= HELPER METHODS ===========================

    private void checKOk()
    {
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia));
    }
}
