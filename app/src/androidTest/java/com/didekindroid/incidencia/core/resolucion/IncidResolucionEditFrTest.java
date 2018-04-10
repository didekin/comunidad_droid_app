package com.didekindroid.incidencia.core.resolucion;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.app.TaskStackBuilder.create;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFrTest.doResolucionAvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkDataResolucionEditFr;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionEditFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionEditFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incideEditMaxPowerFrLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.SPAIN_LOCALE;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeToString;
import static com.didekindroid.router.DidekinUiExceptionAction.show_incid_open_list;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtil.reSetDatePicker;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.fail;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/03/16
 * Time: 17:49
 * <p>
 * Preconditions:
 * 1. A user WITH powers to edit a resolucion is received.
 * 2. A resolucion in BD and intent.
 * 4. Incidencia is OPEN.
 */
@RunWith(AndroidJUnit4.class)
public class IncidResolucionEditFrTest {

    private IncidResolucionEditAc activity;
    private IncidImportancia incidImportancia;
    private Resolucion resolucion;

    @Before
    public void setUp() throws Exception
    {
        try {
            incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
            assertThat(incidImportancia.getUserComu().hasAdministradorAuthority(), is(true));
        } catch (IOException | UiException e) {
            fail();
        }

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
            create(getTargetContext()).addNextIntentWithParentStack(intent1).startActivities();
        }
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_JUAN);
    }

    /*    ============================  TESTS  ===================================*/

    @Test
    public void testOnCreate_1() throws Exception
    {
        // Precondition: with avances.
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(doResolucionAvances(incidImportancia)));
        // Check.
        assertThat(resolucion.getAvances().size(), is(1));
        checkScreenResolucionEditFr(resolucion);
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

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void testOnCreate_2() throws Exception
    {
        // Precondition: NO avances.
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(insertGetResolucionNoAdvances(incidImportancia)));
        // Check.
        checkScreenResolucionEditFr(resolucion);
        checkDataResolucionEditFr(resolucion);
        // Avances.
        onView(allOf(
                withId(android.R.id.empty),
                withText(R.string.incid_resolucion_no_avances_message)
        )).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void testOnEdit_1() throws UiException
    {
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(insertGetResolucionNoAdvances(incidImportancia)));
        /* Caso OK: añadimos un avance con descripción Ok .*/
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance2_desc_válida"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        // Verificamos pantalla de llegada.
        checKIncidAcLayout();

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void testOnEdit_2() throws UiException, InterruptedException
    {
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(doResolucionAvances(incidImportancia)));
        // Caso OK: no cambiamos nada y pulsamos modificar. Mantiene los datos de la resolución.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        checKIncidAcLayout();
        assertThat(incidenciaDao.seeResolucion(resolucion.getIncidencia().getIncidenciaId()), is(resolucion));
        // BACK.
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkBack(onView(withId(incidEditAcLayout)), incidResolucionEditFrLayout);
        }
    }

    @Test
    public void testOnEdit_3() throws UiException, InterruptedException
    {
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(doResolucionAvances(incidImportancia)));
        /* Caso OK: cambiamos la fecha prevista, añadimos un avance con descripción Ok y cambiamos coste (admite importes negativos).*/
        onView(withId(R.id.incid_resolucion_fecha_view)).perform(click());
        Calendar newFechaPrev = reSetDatePicker(resolucion.getFechaPrev().getTime(), 1);
        closeDatePicker(activity);
        // Check date selected.
        if (Locale.getDefault().equals(SPAIN_LOCALE)) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(newFechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance_desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        // Run.
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());
        // Check.
        checKIncidAcLayout();
    }

    @Test
    public void testOnEdit_4() throws UiException, InterruptedException
    {
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(doResolucionAvances(incidImportancia)));
        // Caso NO OK: descripción de avance errónea.
        onView(withId(R.id.incid_resolucion_avance_ed)).perform(replaceText("avance * no válido"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("-1234,5"));
        onView(withId(R.id.incid_resolucion_fr_modif_button)).perform(click());

        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.incid_resolucion_avance_rot));
    }

    @Test
    public void testCloseIncidenciaAndBack() throws InterruptedException, UiException
    {
        activity = (IncidResolucionEditAc) getInstrumentation().startActivitySync(doIntent(doResolucionAvances(incidImportancia)));
        // OK: cerramos la incidencia, damos back y volvemos a intentar cerrarla.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        // BACK
        checkBack(onView(withId(incidSeeByComuAcLayout)), incidResolucionEditFrLayout);
        // Error al intentar borrar otra vez la incidencia.
        onView(withId(R.id.incid_resolucion_edit_fr_close_button)).perform(click());
        waitAtMost(4, SECONDS).until(isToastInView(show_incid_open_list.getResourceIdForToast(), activity));
        onView(withId(incidSeeByComuAcLayout)).check(matches(isDisplayed()));
    }

    /*    ============================= HELPER METHODS ===========================*/

    private void checKIncidAcLayout()
    {
        onView(withId(incidEditAcLayout)).check(matches(isDisplayed()));
        onView(withId(incideEditMaxPowerFrLayout)).check(matches(isDisplayed()));
    }

    @NonNull
    private Intent doIntent(Resolucion resolucionIn)
    {
        Intent intent = new Intent(getTargetContext(), IncidResolucionEditAc.class).setFlags(FLAG_ACTIVITY_NEW_TASK);
        resolucion = resolucionIn;
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        intent.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
        return intent;
    }
}
