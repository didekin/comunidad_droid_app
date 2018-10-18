package com.didekindroid.incidencia.core.resolucion;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.util.UiUtil;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Locale;

import static android.app.TaskStackBuilder.create;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;
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
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionRegFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incideEditMaxPowerFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeToString;
import static com.didekindroid.lib_one.util.UiUtil.isCalendarPreviousTimeStamp;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.reSetDatePicker;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_JUAN;
import static java.util.Objects.requireNonNull;
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
public class IncidResolucionRegAcTest {

    private IncidResolucionRegAc activity;
    private static IncidImportancia incidImportancia;
    private TaskStackBuilder taskStackBuilder;

    @Rule
    public IntentsTestRule<IncidResolucionRegAc> testRule = new IntentsTestRule<IncidResolucionRegAc>(IncidResolucionRegAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            if (SDK_INT >= LOLLIPOP) {
                Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                taskStackBuilder = create(getTargetContext());
                taskStackBuilder.addNextIntent(intent1).startActivities();
            }
            return new Intent().putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        }
    };

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        // A user WITH powers 'adm'.
        incidImportancia = insertGetIncidImportancia(COMU_PLAZUELA5_JUAN);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = testRule.getActivity();
        Intent[] intents = taskStackBuilder.getIntents();
        assertThat(intents.length > 0, is(true));
    }

    @After
    public void tearDown() throws Exception
    {
        if (SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_JUAN);
    }

    //  ===============================  TESTS ================================

    @Test
    public void testOnCreate()
    {
        checkScreenResolucionRegFr();

        // DatePicker tests.
        onView(withId(R.id.incid_resolucion_fecha_view)).check(matches(isDisplayed())).perform(click());
        onView(withClassName(is(DatePicker.class.getName()))).inRoot(isDialog()).check(matches(isDisplayed()));
        // Seleccionamos fecha de resolución: fecha de hoy, sin añadir ningún mes adicional.
        Calendar fechaPrev = reSetDatePicker(0, 0);
        closeDatePicker(activity);

        if (Locale.getDefault().equals(UiUtil.SPAIN_LOCALE) && SDK_INT < M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(fechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        if (Locale.getDefault().equals(UiUtil.SPAIN_LOCALE) && SDK_INT >= M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(fechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }

        if (SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
    }

    @Test
    public void test_registerResolucion()
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

        /*Caso: OK*/
        onView(withId(R.id.incid_resolucion_desc_ed)).perform(replaceText("desc_válida"));
        onView(withId(R.id.incid_resolucion_coste_prev_ed)).perform(replaceText("1234,5"));
        setFechaEnPicker(0, 2);
        // Run
        onView(withId(R.id.incid_resolucion_reg_ac_button)).perform(click());
        // Check.
        checkRegResolucionOk();
    }

    @Test
    public void testOnStop()
    {
        // Check OnStop.
        IncidResolucionRegFr fr = (IncidResolucionRegFr) activity.getSupportFragmentManager().findFragmentByTag(IncidResolucionRegFr.class.getName());
        requireNonNull(fr).controller = new CtrlerIncidenciaCore();
        checkSubscriptionsOnStop(activity, fr.controller);
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
        waitAtMost(8, SECONDS).until(() -> isViewDisplayed(onView(withId(incidEditAcLayout))).call());
        onView(withId(incideEditMaxPowerFrLayout)).check(matches(isDisplayed()));
        // hasResolucion == true, because it has been registered.
        intended(hasExtra(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, true)));
    }
}