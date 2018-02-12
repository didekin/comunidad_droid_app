package com.didekindroid.incidencia.core.resolucion;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.didekindroid.R;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.util.UIutils;
import com.didekindroid.router.UiExceptionRouter;
import com.didekindroid.usuario.firebase.CtrlerFirebaseToken;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkScreenResolucionRegFr;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incideEditMaxPowerFrLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.util.UIutils.formatTimeToString;
import static com.didekindroid.lib_one.util.UIutils.isCalendarPreviousTimeStamp;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtils.closeDatePicker;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.reSetDatePicker;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
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
    TaskStackBuilder taskStackBuilder;
    UiExceptionRouter uiExceptionRouter = (UiExceptionRouter) uiException_router;

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

            if (SDK_INT >= LOLLIPOP) {
                Intent intent1 = new Intent(getTargetContext(), IncidSeeByComuAc.class).putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                taskStackBuilder = create(getTargetContext());
                taskStackBuilder.addNextIntent(intent1).startActivities();
            }
            return new Intent().putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        }
    };

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
        cleanOptions(CLEAN_JUAN);
    }

    //  ===============================  TESTS ================================
    @Test
    public void testOnCreate_1() throws Exception
    {
        checkScreenResolucionRegFr();
        if (SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
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

        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && SDK_INT < M) {
            onView(allOf(
                    withId(R.id.incid_resolucion_fecha_view),
                    withText(formatTimeToString(fechaPrev.getTimeInMillis()))
            )).check(matches(isDisplayed()));
        }
        if (Locale.getDefault().equals(UIutils.SPAIN_LOCALE) && SDK_INT >= M) {
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

        if (SDK_INT >= LOLLIPOP) {
            checkUp(incidSeeByComuAcLayout);
        }
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

    @Test
    public void test_registerResolucion_4()
    {
        // Test of RESOLUCION_DUPLICATE excepction.
        IncidResolucionRegFr fr = (IncidResolucionRegFr) activity.getSupportFragmentManager().findFragmentByTag(IncidResolucionRegFr.class.getName());
        IncidResolucionRegFr.ResolucionRegister resolucionRegister = fr.new ResolucionRegister() {
            @Override
            protected Integer doInBackground(Resolucion... params)
            {
                uiException = new UiException(new ErrorBean(RESOLUCION_DUPLICATE.getHttpMessage(), RESOLUCION_DUPLICATE.getHttpStatus()));
                return 0;
            }
        };
        resolucionRegister.doInBackground(new Resolucion.ResolucionBuilder(incidImportancia.getIncidencia()).buildAsFk());
        activity.runOnUiThread(() -> resolucionRegister.onPostExecute(0));
        waitAtMost(2, SECONDS).until(
                isToastInView(
                        uiExceptionRouter.getActionFromMsg(resolucionRegister.uiException.getErrorHtppMsg()).getResourceIdForToast(),
                        activity
                )
        );
    }

    @Test
    public void test_OnStart() throws Exception
    {
        CtrlerFirebaseTokenIf controller = CtrlerFirebaseToken.class.cast(activity.viewerFirebaseToken.getController());
        TimeUnit.SECONDS.sleep(4);
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }

    @Test
    public void test_OnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity, activity.viewerFirebaseToken.getController());
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