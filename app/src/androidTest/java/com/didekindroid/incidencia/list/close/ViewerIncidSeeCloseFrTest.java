package com.didekindroid.incidencia.list.close;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.api.CtrlerSelectListIf;
import com.didekindroid.api.ViewerSelectListIf;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidenciaUsers;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.checkIncidClosedListView;
import static com.didekindroid.incidencia.testutils.IncidEspressoTestUtils.isComuSpinnerWithText;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidResolucionSeeFrLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeGenericFrLayout;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_ID_LIST_SELECTED;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/03/17
 * Time: 18:08
 */
@RunWith(AndroidJUnit4.class)
public class ViewerIncidSeeCloseFrTest {

    IncidImportancia incidImportancia1;
    Resolucion resolucion;

    @Rule
    public IntentsTestRule<IncidSeeClosedByComuAc> activityRule = new IntentsTestRule<IncidSeeClosedByComuAc>(IncidSeeClosedByComuAc.class, true) {

        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
                incidImportancia1 = makeRegGetIncidImportancia(userComuDaoRemote.seeUserComusByUser().get(0), (short) 1);
                // Cierre incidencias..
                resolucion = insertGetResolucionNoAdvances(incidImportancia1);
                incidenciaDao.closeIncidencia(resolucion);
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
            } catch (UiException | IOException e) {
                fail();
            }
        }
    };

    IncidSeeCloseByComuFr fragment;
    IncidSeeClosedByComuAc activity;

    //    ============================  STATIC UTILITIES  ===================================

    public static void checkOnSuccessLoadItems(IncidImportancia incidImportancia, Activity activity,
                                               final ViewerSelectListIf<ListView, CtrlerSelectListIf<IncidenciaUser>, IncidenciaUser> viewer)
    {
        final List<IncidenciaUser> list = doIncidenciaUsers(incidImportancia);

        activity.runOnUiThread(() -> {
            viewer.setItemSelectedId(22L);
            viewer.onSuccessLoadItemList(list);
            assertThat(viewer.getViewInViewer().getHeaderViewsCount(), is(1));
            // ListView.getCount() and Adapter.getCount() take into account header views.
            assertThat(viewer.getViewInViewer().getCount(), is(4));
            assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(4));
            assertThat(viewer.getViewInViewer().getCheckedItemPosition(), is(3));
            assertThat(viewer.getViewInViewer().getItemAtPosition(viewer.getViewInViewer().getCheckedItemPosition()),
                    allOf(notNullValue(), instanceOf(IncidenciaUser.class)));

        });

        activity.runOnUiThread(() -> {
            viewer.setItemSelectedId(0L);
            viewer.onSuccessLoadItemList(list);
            // When itemSelectedId == 0, no checkedItem.
            assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));

        });

        final List<IncidenciaUser> listEmpty = new ArrayList<>(0);
        activity.runOnUiThread(() -> {
            viewer.setItemSelectedId(22L);
            viewer.onSuccessLoadItemList(listEmpty);
            // No se cumple la condición view.getCount() > view.getHeaderViewsCount(): no se llama  view.setItemChecked().
            assertThat(viewer.getViewInViewer().getCount() <= viewer.getViewInViewer().getHeaderViewsCount(), is(true));
            // When list is empty, no checkedItem.
            assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        });
    }

    //    ============================  SETUP - CLEAN  ===================================

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager().findFragmentByTag(IncidSeeCloseByComuFr.class.getName());
        // Wait until list is made.
        waitAtMost(3, SECONDS).until(isViewDisplayedAndPerform(checkIncidClosedListView(incidImportancia1, activity)));
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================  TESTS INTEGRATION  ===================================

    @Test
    public void testDoViewInViewer() throws Exception
    {
        // itemSelectedId with default value.
        assertThat(fragment.viewer.getSelectedItemId(), is(0L));
        // When itemSelectedId == 0, no checkedItem.
        assertThat(fragment.viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        assertThat(fragment.viewer.getViewInViewer().getOnItemClickListener(), instanceOf(ViewerIncidSeeCloseFr.ListItemOnClickListener.class));
        // Comunidad spinner.
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
    }

    @Test
    public void testListItemOnClickListener() throws InterruptedException
    {
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        assertThat(fragment.viewer.getSelectedItemId(), is(incidImportancia1.getIncidencia().getIncidenciaId()));
        waitAtMost(2, SECONDS).until(isViewDisplayedAndPerform(withId(incidResolucionSeeFrLayout)));
    }

    //    ============================ UNIT TESTS  ===================================

    @Test
    public void testNewViewerIncidSeeClose() throws Exception
    {
        assertThat(fragment.viewer.getController(), allOf(notNullValue(), instanceOf(CtrlerIncidSeeCloseByComu.class)));
        assertThat(fragment.viewer.comuSpinnerViewer, notNullValue());
        assertThat(fragment.viewer.getViewInViewer(), instanceOf(ListView.class));
        assertThat(fragment.viewer.getViewInViewer().getEmptyView(), instanceOf(TextView.class));
    }

    @Test
    public void test_InitSelectedItemId() throws Exception
    {
        Bundle savedState = new Bundle();
        savedState.putLong(INCIDENCIA_ID_LIST_SELECTED.key, 11L);
        fragment.viewer.initSelectedItemId(savedState);

        assertThat(fragment.viewer.getSelectedItemId(), is(11L));

        savedState = new Bundle(0);
        fragment.viewer.initSelectedItemId(savedState);
        assertThat(fragment.viewer.getSelectedItemId(), is(0L));
    }

    @Test
    public void test_OnSuccessLoadItems() throws Exception
    {
        checkOnSuccessLoadItems(incidImportancia1, activity, fragment.viewer);
    }

    @Test
    public void test_OnSuccessLoadSelectedItem() throws Exception
    {
        // Preconditions.
        Bundle bundle = new Bundle(3);
        bundle.putBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, true);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, resolucion.getIncidencia());
        bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        // Exec.
        fragment.viewer.onSuccessLoadSelectedItem(bundle);
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(incidResolucionSeeFrLayout)));
        // Checkup
        checkUp(incidSeeGenericFrLayout);
    }

    @Test
    public void test_GetSelectedPositionFromItemId() throws Exception
    {
        final List<IncidenciaUser> list = doIncidenciaUsers(incidImportancia1);

        activity.runOnUiThread(() -> {
            fragment.viewer.onSuccessLoadItemList(list);
            assertThat(fragment.viewer.getSelectedPositionFromItemId(33L), is(1));
            assertThat(fragment.viewer.getSelectedPositionFromItemId(11L), is(2));
            assertThat(fragment.viewer.getSelectedPositionFromItemId(22L), is(3));
            // No se encuentra la incidencia en la lista.
            assertThat(fragment.viewer.getSelectedPositionFromItemId(93L), is(0));
        });
    }

    //    ============================ LIFE CYCLE TESTS  ===================================

    @Test
    public void testClearSubscriptions() throws Exception
    {
        checkSubscriptionsOnStop(activity, fragment.viewer.comuSpinnerViewer.getController(),
                fragment.viewer.getController());
    }

    @Test
    public void testSaveState() throws Exception
    {
        Bundle bundle = new Bundle(2);
        fragment.viewer.comuSpinnerViewer.setItemSelectedId(7L);
        fragment.viewer.setItemSelectedId(5L);

        fragment.viewer.saveState(bundle);
        assertThat(bundle.getLong(INCIDENCIA_ID_LIST_SELECTED.key), is(5L));
        assertThat(bundle.getLong(COMUNIDAD_ID.key), is(7L));
    }
}