package com.didekindroid.incidencia.resolucion;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataResolucionEditFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionEditFr;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.reSetDatePicker;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.util.UIutils.SPAIN_LOCALE;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static com.didekindroid.util.UIutils.formatTimeToString;
import static java.lang.Math.abs;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionEditFrTest {

    IncidResolucionEditAc activity;
    IncidImportancia incidImportancia;
    Resolucion resolucion;
    @Rule
    public IntentsTestRule<IncidResolucionEditAc> intentRule = new IntentsTestRule<IncidResolucionEditAc>(IncidResolucionEditAc.class) {
        /**
         * Preconditions:
         * 1. A user WITH powers to edit a resolucion is received.
         * 2. A resolucion in BD and intent.
         * 3. Resolucion WITH avances.
         * 4. Incidencia is OPEN.
         * */
        @Override
        protected Intent getActivityIntent()
        {
            try {
                incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
                // Registramos resolución.
                Thread.sleep(1000);
                resolucion = insertGetResolucionNoAdvances(incidImportancia);
                // Modificamos resolución.
                Avance avance = new Avance.AvanceBuilder().avanceDesc("avance1_desc").build();
                List<Avance> avances = new ArrayList<>(1);
                avances.add(avance);
                resolucion = new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia())
                        .copyResolucion(resolucion)
                        .avances(avances)
                        .build();
                assertThat(incidenciaDao.modifyResolucion(resolucion), is(2));
                resolucion = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
            } catch (InterruptedException | IOException | UiException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
            return intent;
        }
    };
    Resolucion resolucionIntent;

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_JUAN);
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        assertThat(resolucionIntent.getAvances().size(), is(1));
        checkScreenResolucionEditFr(resolucionIntent);
    }

    @Test
    public void testOnData_1()
    {
        checkDataResolucionEditFr(resolucion);
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
                        withText(USER_JUAN.getAlias()) // usuario en sesión que modifica resolución.
                )))).check(matches(isDisplayed()));
    }

    @Test
    public void testOnEdit_1() throws UiException
    {
        // Caso OK: añadimos un avance con descripción Ok .
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance2_desc_válida"));

        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        // Verificamos pantalla de llegada, intent y BD.
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, resolucion != null)));
        Resolucion resolucionDb = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(2));
        assertThat(resolucionDb.getAvances().get(1).getAvanceDesc(), is("avance2_desc_válida"));

        checkUp();
        checkScreenResolucionEditFr(resolucionIntent);
        checkDataResolucionEditFr(resolucion);
    }

    @Test
    public void testCloseIncidenciaAndBack() throws InterruptedException
    {
        // Caso NOT OK: cerramos la incidencia, damos back y volvemos a intentar cerrarla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed())).perform(pressBack());

        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    /**
     * Preconditions:
     * 1. A user WITH powers 'adm' in sesssion.
     * 2. A resolucion in BD and intent.
     * 3. Resolucion WITHOUT avances.
     */

    // --------------
    @Test
    public void testOnCreate_2() throws Exception
    {
        checkScreenResolucionEditFr(resolucionIntent);
        checkDataResolucionEditFr(resolucion);
        // Avances.
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));
        // TODO: checkUp a edición incidencia.
    }

    @Test
    public void testOnEdit_2() throws UiException
    {
        // Caso OK: no cambiamos nada y pulsamos modificar. Mantiene los datos de la resolución.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk(); // TODO: cambiar a check de edición de la resolución no de la incidencia.
        Resolucion resolucionDb = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.equals(resolucion), is(true));
        assertThat(abs(resolucionDb.getFechaPrev().getTime() - resolucion.getFechaPrev().getTime()) < 1000, is(true));

        checkUp();
        checkScreenResolucionEditFr(resolucionIntent);
        checkDataResolucionEditFr(resolucion);
    }

    @Test
    public void testOnEdit_3() throws UiException
    {
        // Caso OK: cambiamos la fecha prevista.

        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar newFechaPrev = reSetDatePicker(resolucion.getFechaPrev().getTime(), 1);
        closeDatePicker(activity);

        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(newFechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKOk();

        checkUp();
        checkScreenResolucionEditFr(resolucionIntent);
    }

    @Test
    public void testOnEdit_4() throws UiException
    {
        // Caso OK: añadimos un avance con descripción Ok y cambiamos coste (admite importes negativos).
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance_desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        checKOk();
        Resolucion resolucionDb = incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId());
        assertThat(resolucionDb.getAvances().size(), is(1));
        assertThat(resolucionDb.getAvances().get(0).getAvanceDesc(), is("avance_desc_válida"));
        assertThat(resolucionDb.getCosteEstimado(), is(-1234));

        checkUp();
        checkScreenResolucionEditFr(resolucionIntent);
    }

    @Test
    public void testOnEdit_5() throws UiException, InterruptedException
    {
        // Caso NO OK: descripción de avance errónea.
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance * no válido"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_resolucion_avance_rot));
    }

    @Test
    public void testCloseIncidenciaAndBack_2() throws InterruptedException
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));

        // Damos back e intentamos modificar la incidencia. Nos da error.
        checkBack(onView(withId(R.id.incid_see_closed_by_comu_ac)), R.id.incid_resolucion_fr_modif_button);
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

    @Test
    public void testCloseIncidenciaAndUp() throws InterruptedException
    {
        // Caso OK: cerramos incidencia sin cambiar datos en pantalla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        onView(withId(R.id.incid_see_closed_by_comu_ac)).check(matches(isDisplayed()));

        // Up Navigate.
        checkUp();
        checkScreenResolucionEditFr(resolucionIntent);
        checkDataResolucionEditFr(resolucion);

        // Intentamos modificar la incidencia: error nos manda a la consulta.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        intended(hasExtra(COMUNIDAD_ID.key, incidImportancia.getIncidencia().getComunidadId()));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.incidencia_wrong_init, activity));
        onView(withId(R.id.incid_see_open_by_comu_ac)).check(matches(isDisplayed()));
    }

/*    ============================= HELPER METHODS ===========================*/

    private void checKOk()
    {
        onView(withId(R.id.incid_edit_fragment_container_ac)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_edit_maxpower_fr_layout)).check(matches(isDisplayed()));
        intended(hasExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, resolucion != null)));
    }
}
