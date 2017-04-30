package com.didekindroid.incidencia.list.close;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.api.CtrlerSelectableItemIf;
import com.didekindroid.api.ViewerSelectionListIf;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.close.ViewerIncidSeeClose.newViewerIncidSeeClose;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doSimpleIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeRegGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.nextResolucionFrResourceId;
import static com.didekindroid.incidencia.testutils.IncidUiTestUtils.checkIncidClosedListView;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_ID_LIST_SELECTED;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceComponent;
import static com.didekindroid.testutil.ActivityTestUtils.isComuSpinnerWithText;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
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
public class ViewerIncidSeeCloseTest {

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

    ViewerIncidSeeClose viewer;
    IncidSeeCloseByComuFr fragment;
    IncidSeeClosedByComuAc activity;

    @NonNull
    public static List<IncidenciaUser> doIncidenciaUsers(IncidImportancia incidImportancia)
    {
        final List<IncidenciaUser> list = new ArrayList<>(3);
        Timestamp resolucionDate = incidImportancia.getIncidencia().getFechaCierre();
        Timestamp altaIncidDate = incidImportancia.getIncidencia().getFechaAlta();
        IncidenciaUser iu_1 = doSimpleIncidenciaUser(33L, altaIncidDate, 34L, resolucionDate);
        IncidenciaUser iu_2 = doSimpleIncidenciaUser(11L, altaIncidDate, 14L, resolucionDate);
        IncidenciaUser iu_3 = doSimpleIncidenciaUser(22L, altaIncidDate, 24L, resolucionDate);
        list.add(iu_1);
        list.add(iu_2);
        list.add(iu_3);
        return list;
    }

    //    ============================  STATIC UTILITIES  ===================================

    public static void checkOnSuccessLoadItems(IncidImportancia incidImportancia, Activity activity,
                                               final ViewerSelectionListIf<ListView, CtrlerSelectableItemIf<IncidenciaUser, Bundle>, IncidenciaUser> viewer)
    {
        final List<IncidenciaUser> list = doIncidenciaUsers(incidImportancia);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.setItemSelectedId(22L);
                viewer.onSuccessLoadItems(list);
                assertThat(viewer.getViewInViewer().getHeaderViewsCount(), is(1));
                // ListView.getCount() and Adapter.getCount() take into account header views.
                assertThat(viewer.getViewInViewer().getCount(), is(4));
                assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(4));
                assertThat(viewer.getViewInViewer().getCheckedItemPosition(), is(3));
                assertThat(viewer.getViewInViewer().getItemAtPosition(viewer.getViewInViewer().getCheckedItemPosition()),
                        allOf(notNullValue(), instanceOf(IncidenciaUser.class)));

            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.setItemSelectedId(0L);
                viewer.onSuccessLoadItems(list);
                // When itemSelectedId == 0, no checkedItem.
                assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));

            }
        });

        final List<IncidenciaUser> listEmpty = new ArrayList<>(0);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.setItemSelectedId(22L);
                viewer.onSuccessLoadItems(listEmpty);
                // No se cumple la condición view.getCount() > view.getHeaderViewsCount(): no se llama  view.setItemChecked().
                assertThat(viewer.getViewInViewer().getCount() <= viewer.getViewInViewer().getHeaderViewsCount(), is(true));
                // When list is empty, no checkedItem.
                assertThat(viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
            }
        });
    }

    //    ============================  SETUP - CLEAN  ===================================

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        fragment = (IncidSeeCloseByComuFr) activity.getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
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
        // Check list.
        waitAtMost(3, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // itemSelectedId with default value.
        assertThat(fragment.viewer.getSelectedItemId(), is(0L));
        // When itemSelectedId == 0, no checkedItem.
        assertThat(fragment.viewer.getViewInViewer().getCheckedItemPosition() < 0, is(true));
        assertThat(fragment.viewer.getViewInViewer().getOnItemClickListener(), instanceOf(ViewerIncidSeeClose.ListItemOnClickListener.class));
        // Comunidad spinner.
        waitAtMost(2, SECONDS).until(isComuSpinnerWithText(incidImportancia1.getIncidencia().getComunidad().getNombreComunidad()));
    }

    @Test
    public void testListItemOnClickListener() throws InterruptedException
    {
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        onData(isA(IncidenciaUser.class)).inAdapterView(withId(android.R.id.list))
                .check(matches(isDisplayed()))
                .perform(click());
        assertThat(fragment.viewer.getSelectedItemId(), is(incidImportancia1.getIncidencia().getIncidenciaId()));
        waitAtMost(2, SECONDS).until(isViewDisplayed(withId(nextResolucionFrResourceId)));
    }

    @Test
    public void testReplaceComponent() throws Exception
    {
        waitAtMost(2, SECONDS).until(isViewDisplayed(checkIncidClosedListView(incidImportancia1, activity)));
        // Preconditions.
        Bundle bundle = new Bundle(3);
        bundle.putBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, true);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, resolucion.getIncidencia());
        bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);

        checkViewerReplaceComponent(fragment.viewer, nextResolucionFrResourceId, bundle);
    }

    //    ============================  TESTS  ===================================

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
        viewer = newViewerIncidSeeClose(fragment.getView(), activity);
        viewer.initSelectedItemId(savedState);

        assertThat(viewer.getSelectedItemId(), is(11L));

        savedState = new Bundle(0);
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(0L));
    }

    @Test
    public void test_OnSuccessLoadItems() throws Exception    // getHeaderViewsCount()
    {
        checkOnSuccessLoadItems(incidImportancia1, activity, fragment.viewer);
    }

    @Test
    public void test_GetSelectedPositionFromItemId() throws Exception
    {
        final List<IncidenciaUser> list = doIncidenciaUsers(incidImportancia1);
        viewer = newViewerIncidSeeClose(fragment.getView(), activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItems(list);
                assertThat(viewer.getSelectedPositionFromItemId(33L), is(1));
                assertThat(viewer.getSelectedPositionFromItemId(11L), is(2));
                assertThat(viewer.getSelectedPositionFromItemId(22L), is(3));
                // No se encuentra la incidencia en la lista.
                assertThat(viewer.getSelectedPositionFromItemId(93L), is(0));
            }
        });
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        viewer = newViewerIncidSeeClose(fragment.getView(), activity);

        addSubscription(viewer.comuSpinnerViewer.getController());
        addSubscription(viewer.getController());

        assertThat(viewer.clearSubscriptions(), is(0));
        assertThat(viewer.comuSpinnerViewer.getController().getSubscriptions().size(), is(0));
        assertThat(viewer.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testSaveState() throws Exception
    {
        viewer = newViewerIncidSeeClose(fragment.getView(), activity);

        Bundle bundle = new Bundle(2);
        viewer.comuSpinnerViewer.setItemSelectedId(7L);
        viewer.setItemSelectedId(5L);

        viewer.saveState(bundle);
        assertThat(bundle.getLong(INCIDENCIA_ID_LIST_SELECTED.key), is(5L));
        assertThat(bundle.getLong(COMUNIDAD_ID.key), is(7L));
    }
}