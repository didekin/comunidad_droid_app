package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListView;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListViewNoResol;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAdvances;
import static com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/03/16
 * Time: 15:41
 * <p>
 * Preconditions:
 * 1. Incidencia with resolucion in BD.
 * Condition:
 * 1. User select an incidencia.
 * Postconditions:
 * 1. The incidencia is shown in edit mode.
 * 2. An intent is passed whith:
 * - the incidImportancia instance of the user in session.
 * - a true value in the resolucion flag.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class IncidSeeByComuAc_Open_Test {

    private IncidImportancia incidImportancia1;
    private IncidImportancia incidImportancia2;
    private IncidenciaUser incidenciaUser1;
    private IncidSeeByComuAc activity;
    private IncidSeeByComuFr fragment;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
            incidImportancia1 = insertGetIncidImportancia(userComuDao.seeUserComusByUser().blockingGet().get(0), (short) 1);
            incidImportancia2 = insertGetIncidImportancia(userComuDao.seeUserComusByUser().blockingGet().get(1), (short) 4);
            // Resolución para incidencia1.
            insertGetResolucionNoAdvances(incidImportancia1);
            incidenciaUser1 = incidenciaDao.seeIncidsOpenByComu(incidImportancia1.getIncidencia().getComunidadId()).blockingGet().get(0);
            return new Intent().putExtra(INCID_CLOSED_LIST_FLAG.key, false);
        }
    };


    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(IncidSeeByComuFr.class.getName());
        assertThat(fragment, notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        activity.deleteDatabase(DB_NAME);
        cleanOptions(CLEAN_PEPE);
    }

    //  ==================================== INTEGRATION TESTS  ====================================

    @Test
    public void testOnCreate()
    {
        onView(withId(incidSeeByComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(incidSeeGenericFrLayout)).check(matches(isDisplayed()));
        // Inicializa correctamente fragment: 0, porque no hay intent en activity.
        assertThat(fragment.getArguments().getLong(COMUNIDAD_ID.key), is(0L));
        assertThat(fragment.getArguments().getBoolean(INCID_CLOSED_LIST_FLAG.key), is(false));
        // Rótulo
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_see_by_user_ac_label)));
        // FloatingButton
        onView(withId(R.id.incid_new_incid_fab)).check(matches(isDisplayed()));
        // CASO Visibilidad del bloque de datos de resolución.
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
        // CASO NO visibilidad del bloque de datos de resolución.
        // Cambiamos la comunidad en el spinner y revisamos los datos.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidOpenListViewNoResol()));
    }

    @Test
    public void testOnSelected_Up_1()
    {
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        // Seleccionamos incidencia (CON RESOLUCION).
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidEditAcLayout)));
        // Check intent.
        intended(hasExtras(
                hasEntry(INCID_RESOLUCION_BUNDLE.key,
                        allOf(
                                isA(IncidAndResolBundle.class),
                                equalTo(new IncidAndResolBundle(incidImportancia1, true))
                        )
                )
        ));
        // Up and checkMenu.
        checkUp(incidSeeByComuAcLayout);
        waitAtMost(6, SECONDS)
                .until(isViewDisplayed(checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
    }

    @Test
    public void testOnSelected_2() throws InterruptedException
    {
        waitAtMost(6, SECONDS).until(isViewDisplayed(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        // Cambiamos la comunidad en el spinner.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        TimeUnit.SECONDS.sleep(1);
        // Seleccionamos incidencia (SIN RESOLUCION)
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(6, SECONDS).until(isViewDisplayed(withId(incidEditAcLayout)));
        // Check intent.
        intended(hasExtras(
                hasEntry(INCID_RESOLUCION_BUNDLE.key,
                        allOf(
                                isA(IncidAndResolBundle.class),
                                equalTo(new IncidAndResolBundle(incidImportancia2, false))
                        )
                )
        ));
    }

    @Test
    public void testOnSelected_Back_3()
    {
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidEditAcLayout)));
        // Back and checkMenu.
        checkBack(onView(withId(incidEditAcLayout)));
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
    }
}