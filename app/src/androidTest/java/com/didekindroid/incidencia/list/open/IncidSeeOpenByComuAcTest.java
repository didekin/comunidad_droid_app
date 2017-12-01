package com.didekindroid.incidencia.list.open;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
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
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.core.IncidenciaDataDbHelper.DB_NAME;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListView;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListViewNoResol;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeOpenAcLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ActivityTestUtils.checkBack;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regSeveralUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
public class IncidSeeOpenByComuAcTest {

    IncidImportancia incidImportancia1;
    IncidImportancia incidImportancia2;
    IncidenciaUser incidenciaUser1;
    IncidenciaUser incidenciaUser2;

    @Rule
    public IntentsTestRule<IncidSeeOpenByComuAc> activityRule = new IntentsTestRule<IncidSeeOpenByComuAc>(IncidSeeOpenByComuAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE);
                incidImportancia1 = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 1);
                incidImportancia2 = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(1), (short) 4);
                // Resolución para incidencia1.
                insertGetResolucionNoAdvances(incidImportancia1);
                incidenciaUser1 = incidenciaDao.seeIncidsOpenByComu(incidImportancia1.getIncidencia().getComunidadId()).get(0);
                incidenciaUser2 = incidenciaDao.seeIncidsOpenByComu(incidImportancia2.getIncidencia().getComunidadId()).get(0);

            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    IncidSeeOpenByComuAc activity;
    IncidSeeOpenByComuFr fragment;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeOpenByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(IncidSeeOpenByComuFr.class.getName());
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
        onView(withId(incidSeeOpenAcLayout)).check(matches(isDisplayed()));
        onView(withId(incidSeeGenericFrLayout)).check(matches(isDisplayed()));
        // Inicializa correctamente fragment: 0, porque no hay intent en activity.
        assertThat(fragment.getArguments().getLong(COMUNIDAD_ID.key), is(0L));

        // CASO Visibilidad del bloque de datos de resolución.
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));

        // CASO NO visibilidad del bloque de datos de resolución.
        // Cambiamos la comunidad en el spinner y revisamos los datos.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        waitAtMost(2, SECONDS).until(isViewDisplayedAndPerform(checkIncidOpenListViewNoResol()));
    }

    @Test
    public void testOnSelected_Up() throws Exception
    {
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(incidEditAcLayout)));
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
        checkUp(incidSeeOpenAcLayout);
        isViewDisplayedAndPerform(checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())).call();
    }

    @Test
    public void testOnSelectedNoResolucion() throws UiException, InterruptedException
    {
        waitAtMost(3, SECONDS).until(isViewDisplayed(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));

        // Cambiamos la comunidad en el spinner.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        TimeUnit.SECONDS.sleep(1);
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());

        // Check next fragment.
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(withId(incidEditAcLayout)));
        // Check intent.
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
    public void testOnSelected_Back() throws UiException
    {
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(2, SECONDS).until(isViewDisplayedAndPerform(withId(incidEditAcLayout)));
        // Back and checkMenu.
        checkBack(onView(withId(incidEditAcLayout)));
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
    }

    @Test
    public void test_newIncidenciaButton() throws InterruptedException
    {
        waitAtMost(3, SECONDS).until(isViewDisplayed(
                checkIncidOpenListView(incidImportancia1, activity, incidenciaUser1.getFechaAltaResolucion())));
        waitAtMost(6, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.incid_new_incid_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidRegAcLayout));
        checkUp(incidSeeOpenAcLayout);
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, fragment.viewer.getController());
    }
}