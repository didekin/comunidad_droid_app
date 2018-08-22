package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidOpenListView;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidEditAcLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAdvances;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.isStatementTrue;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/03/17
 * Time: 18:10
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeOpenFrTest {

    private Resolucion resolucion;
    private IncidenciaUser incidenciaUser;
    private IncidImportancia incidImportancia;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regComuUserUserComuGetAuthTk(COMU_ESCORIAL_PEPE);
            } catch (Exception e) {
                fail();
            }
            incidImportancia = insertGetIncidImportancia(userComuDao.seeUserComusByUser().blockingGet().get(0),
                    (short) 2);
            // Cierre incidencias..
            resolucion = insertGetResolucionNoAdvances(incidImportancia);
            incidenciaUser = incidenciaDao
                    .seeIncidsOpenByComu(incidImportancia.getIncidencia().getComunidadId()).blockingGet().get(0);
            return new Intent().putExtra(INCID_CLOSED_LIST_FLAG.key, false);
        }
    };

    private IncidSeeByComuAc activity;
    private IncidSeeByComuFr fragment;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeByComuFr) activity.getSupportFragmentManager()
                .findFragmentByTag(IncidSeeByComuFr.class.getName());
        // Wait until everything is ready.
        waitAtMost(4, SECONDS)
                .until(isViewDisplayed(checkIncidOpenListView(incidImportancia, activity, incidenciaUser.getFechaAltaResolucion())));

    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerIncidSeeOpen()
    {
        assertThat(fragment.viewer.getController(), allOf(
                notNullValue(), instanceOf(CtrlerIncidSeeOpenByComu.class)
        ));
        assertThat(fragment.viewer.getComuSpinner(), notNullValue());

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, fragment.viewer.getController());
    }

    @Test
    public void test_OnSuccessLoadSelectedItem()
    {
        // Exec and check.
        waitAtMost(4, SECONDS).until(isStatementTrue(fragment.viewer != null));
        fragment.viewer.onSuccessLoadSelectedItem(
                INCID_RESOLUCION_BUNDLE.getBundleForKey(new IncidAndResolBundle(incidImportancia, resolucion != null))
        );
        onView(withId(incidEditAcLayout)).check(matches(isDisplayed()));
    }
}