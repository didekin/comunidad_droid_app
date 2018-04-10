package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidClosedListView;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.doComunidadSpinner;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidRegAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionSeeFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeByComuAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayedAndPerform;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_LA_FUENTE_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regSeveralUserComuSameUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 11:35
 */

@SuppressWarnings("ConstantConditions")
public class IncidSeeByComuAc_Close_Test {

    private IncidImportancia incidImportancia2;
    private IncidImportancia incidImportancia1;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regSeveralUserComuSameUser(COMU_PLAZUELA5_PEPE, COMU_LA_FUENTE_PEPE); // Orden en lista: lafuente(0), plazuelas(1).
                incidImportancia1 = makeRegGetIncidImportancia(userComuDao.seeUserComusByUser().get(0), (short) 1);
                incidImportancia2 = makeRegGetIncidImportancia(userComuDao.seeUserComusByUser().get(1), (short) 4);

                // Cierre incidencias..
                incidenciaDao.closeIncidencia(insertGetResolucionNoAdvances(incidImportancia1));
                incidenciaDao.closeIncidencia(insertGetResolucionNoAdvances(incidImportancia2));

                // Incidencias con fecha de cierre.
                incidImportancia1 = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia1.getIncidencia())
                                .fechaCierre(
                                        incidenciaDao.seeIncidsClosedByComu(incidImportancia1.getIncidencia().getComunidadId()).get(0).getIncidencia().getFechaCierre()
                                )
                                .build())
                        .copyIncidImportancia(incidImportancia1)
                        .build();

                incidImportancia2 = new IncidImportancia.IncidImportanciaBuilder(
                        new Incidencia.IncidenciaBuilder()
                                .copyIncidencia(incidImportancia2.getIncidencia())
                                .fechaCierre(
                                        incidenciaDao.seeIncidsClosedByComu(incidImportancia2.getIncidencia().getComunidadId()).get(0).getIncidencia().getFechaCierre()
                                )
                                .build())
                        .copyIncidImportancia(incidImportancia2)
                        .build();

            } catch (UiException | IOException e) {
                fail();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(IncidSeeByComuAc.class).startActivities();
            }

            return new Intent().putExtra(COMUNIDAD_ID.key, incidImportancia1.getIncidencia().getComunidadId()).putExtra(INCID_CLOSED_LIST_FLAG.key, true);
        }
    };

    private IncidSeeByComuAc activity;
    private IncidSeeByComuFr fragment;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeByComuFr) activity.getSupportFragmentManager().findFragmentByTag(IncidSeeByComuFr.class.getName());
    }

    @After
    public void tearDown() throws Exception
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activityRule.getActivity());
        }
        cleanOptions(CLEAN_PEPE);
    }


    //  ==================================== INTEGRATION TESTS  ====================================

    @Test
    public void testOnCreate_1()
    {
        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_closed_by_user_ac_label)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(seeUserComuByUserFrRsId);
        }
    }

    @Test
    public void testOnCreate_2()
    {
        /* CASO OK: muestra las incidencias de la comunidad por defecto (Calle La Fuente).*/
        assertThat(fragment.getArguments().getLong(COMUNIDAD_ID.key), is(incidImportancia1.getIncidencia().getComunidadId()));
        assertThat(fragment.getArguments().getBoolean(INCID_CLOSED_LIST_FLAG.key), is(true));

        assertThat(activity.getTitle(), is(activity.getText(R.string.incid_closed_by_user_ac_label)));
        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withId(incidSeeByComuAcLayout)).check(matches(isDisplayed()));
        onView(withId(incidSeeGenericFrLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.incid_comunidad_spinner)).check(matches(isDisplayed()));

        // Data
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));

        // Cambiamos la comunidad en el spinner y revisamos los datos.
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia2, activity)));
    }

    @Test
    public void test_newIncidenciaButton_1()
    {
        waitAtMost(6, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.incid_new_incid_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidRegAcLayout));
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
        checkUp(incidSeeByComuAcLayout);
    }

    @Test
    public void test_newIncidenciaButton_2()
    {
        waitAtMost(6, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
        doComunidadSpinner(incidImportancia2.getIncidencia().getComunidad());
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.incid_new_incid_fab), click()));
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(incidRegAcLayout));
        waitAtMost(4, SECONDS).until(isComuSpinnerWithText(incidImportancia2.getIncidencia().getComunidad().getNombreComunidad()));
        checkUp(incidSeeByComuAcLayout);
    }

    @Test
    public void testOnSelectedWithUp()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidResolucionSeeFrLayout)));
        // Up and checkMenu.
        checkUp();
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
    }

    @Test
    public void testOnSelectedWithBack()
    {
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // Seleccionamos incidencia.
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        // Check next fragment.
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidResolucionSeeFrLayout)));
        // Back and checkMenu.
        checkBack(onView(withId(incidResolucionSeeFrLayout)));
        waitAtMost(4, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
    }

    //  ======================================== UNIT TESTS  =======================================

    @Test
    public void testOnStop()
    {
        checkSubscriptionsOnStop(activity, fragment.viewer.getController());
    }
}
