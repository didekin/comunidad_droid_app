package com.didekindroid.incidencia.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.incidencia.IncidBundleKey;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_ID_LIST_SELECTED;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidClosedListView;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionSeeFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.testutils.IncidTestData.doIncidenciaUsers;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidTestData.insertGetResolucionNoAvances;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetAuthTk;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isViewDisplayed;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_PLAZUELA5_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/03/17
 * Time: 18:08
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeCloseFrTest {

    private static IncidImportancia incidImportancia1;
    private static Resolucion resolucion;

    @Rule
    public IntentsTestRule<IncidSeeByComuAc> activityRule = new IntentsTestRule<IncidSeeByComuAc>(IncidSeeByComuAc.class, true) {

        @Override
        protected Intent getActivityIntent()
        {
            return new Intent().putExtra(IncidBundleKey.INCID_CLOSED_LIST_FLAG.key, true);
        }
    };

    private IncidSeeByComuFr fragment;
    private IncidSeeByComuAc activity;

    //    ============================  SETUP - CLEAN  ===================================

    @BeforeClass
    public static void setUpStatic() throws Exception
    {
        regComuUserUserComuGetAuthTk(COMU_PLAZUELA5_PEPE);
        incidImportancia1 = insertGetIncidImportancia(userComuDao.seeUserComusByUser().blockingGet().get(0), (short) 1);
        // Cierre incidencias..
        resolucion = insertGetResolucionNoAvances(incidImportancia1);
        incidenciaDao.closeIncidencia(resolucion).blockingGet();
        // Incidencias con fecha de cierre.
        incidImportancia1 = new IncidImportancia.IncidImportanciaBuilder(
                new Incidencia.IncidenciaBuilder()
                        .copyIncidencia(incidImportancia1.getIncidencia())
                        .fechaCierre(
                                incidenciaDao.seeIncidsClosedByComu(incidImportancia1.getIncidencia().getComunidadId())
                                        .blockingGet().get(0).getIncidencia().getFechaCierre()
                        )
                        .build())
                .copyIncidImportancia(incidImportancia1)
                .build();
    }

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeByComuFr) activity.getSupportFragmentManager().findFragmentByTag(IncidSeeByComuFr.class.getName());
        // Wait until list is made. ***** Here is tested the visual display of the data *****.
        waitAtMost(6, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
    }

    @AfterClass
    public static void tearDown()
    {
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================ TESTS  ===================================

    @Test
    public void testDoViewInViewer()
    {
        // testNewViewerIncidSeeClose
        assertThat(fragment.viewer.getController(), allOf(notNullValue(), instanceOf(CtrlerIncidSeeCloseByComu.class)));
        assertThat(fragment.viewer.comuSpinnerViewer, notNullValue());
        assertThat(fragment.viewer.getViewInViewer(), instanceOf(ListView.class));
        assertThat(fragment.viewer.getViewInViewer().getEmptyView(), instanceOf(TextView.class));

        // itemSelectedId with default value.
        assertThat(fragment.viewer.getSelectedItemId(), is(0L));
        // When itemSelectedId == 0, no checkedItem.
        assertThat(fragment.viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        assertThat(fragment.viewer.getViewInViewer().getOnItemClickListener(), instanceOf(ViewerIncidSeeCloseFr.ListItemOnClickListener.class));
        // Comunidad spinner. ***** Here is tested the visual display of the data *****.
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));

        // testSaveState
        Bundle bundle = new Bundle(2);
        fragment.viewer.comuSpinnerViewer.setSelectedItemId(7L);
        fragment.viewer.setSelectedItemId(5L);

        fragment.viewer.saveState(bundle);
        assertThat(bundle.getLong(INCIDENCIA_ID_LIST_SELECTED.key), is(5L));
        assertThat(bundle.getLong(COMUNIDAD_ID.key), is(7L));

        // test_InitSelectedItemId
        Bundle savedState = new Bundle();
        savedState.putLong(INCIDENCIA_ID_LIST_SELECTED.key, 11L);
        fragment.viewer.initSelectedItemId(savedState);

        assertThat(fragment.viewer.getSelectedItemId(), is(11L));

        savedState = new Bundle(0);
        fragment.viewer.initSelectedItemId(savedState);
        assertThat(fragment.viewer.getSelectedItemId(), is(0L));

        // testClearSubscriptions
        checkSubscriptionsOnStop(activity, fragment.viewer.comuSpinnerViewer.getController(),
                fragment.viewer.getController());
    }

    @Test
    public void test_OnSuccessLoadSelectedItem()
    {
        // Preconditions.
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        // Exec.
        fragment.viewer.onSuccessLoadSelectedItem(bundle);
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidResolucionSeeFrLayout)));
        // Checkup
        checkUp(incidSeeGenericFrLayout);
    }

    @Test
    public void test_GetSelectedPositionFromItemId()
    {
        final List<IncidenciaUser> list = doIncidenciaUsers(incidImportancia1);

        fragment.viewer.setSelectedItemId(11L);
        activity.runOnUiThread(() -> {
            fragment.viewer.onSuccessLoadItemList(list);
            // Exec and check: Adapter.getCount() take into account header views (+1).
            assertThat(fragment.viewer.getSelectedPositionFromItemId(fragment.viewer.getBeanIdFunction()), is(2));   // id 11
        });
    }

    @Test
    public void testListItemOnClickListener()
    {
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        assertThat(fragment.viewer.getSelectedItemId(), is(incidImportancia1.getIncidencia().getIncidenciaId()));
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidResolucionSeeFrLayout)));
    }
}