package com.didekindroid.incidencia.list.close;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ViewerIncidListByComu;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.close.CtrlerIncidSeeCloseByComu.bundleWithResolucion;
import static com.didekindroid.incidencia.list.close.CtrlerIncidSeeCloseByComu.incidCloseList;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 13:25
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidSeeCloseByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Resolucion resolucion;
    List<IncidenciaUser> incidList;
    Incidencia incidencia;
    IncidenciaUser incidenciaUser;
    CtrlerIncidSeeCloseByComu controller;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws IOException, UiException, InterruptedException
    {
        Activity activity = activityRule.getActivity();
        controller = new CtrlerIncidSeeCloseByComu(new ViewerIncidSeeForTest(null, null, activity, null));

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        UsuarioComunidad pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
        resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));

        incidList = new ArrayList<>();
        incidencia = resolucion.getIncidencia();
        incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(pepeUserComu.getUsuario()).build();
        incidList.add(incidenciaUser);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    // ......................... OBSERVABLES .............................

    @Test
    public void testResolucion()
    {
        bundleWithResolucion(resolucion.getIncidencia()).test().assertOf(new Consumer<TestObserver<Bundle>>() {
            @Override
            public void accept(TestObserver<Bundle> bundleTestObserver) throws Exception
            {
                Bundle bundleIn = bundleTestObserver.values().get(0);
                assertThat(bundleIn.getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key), is(true));
                assertThat(bundleIn.getSerializable(INCIDENCIA_OBJECT.key), CoreMatchers.<Serializable>is(incidencia));
                assertThat(bundleIn.getSerializable(INCID_RESOLUCION_OBJECT.key), CoreMatchers.<Serializable>is(resolucion));
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIncidCloseList()
    {
        incidCloseList(resolucion.getIncidencia().getComunidadId()).test().assertResult(incidList);
    }

    /* ............................ INSTANCE METHODS ...............................*/

    @Test
    public void testLoadItemsByEntitiyId()
    {
        assertThat(controller.loadItemsByEntitiyId(9L), is(true));
    }

    @Test
    public void testDealWithIncidSelected()
    {
        assertThat(controller.selectItem(incidenciaUser), is(true));
    }

    @Test
    public void testOnSuccessLoadItemsById() throws Exception
    {
        // Precondition:
        assertThat(controller.adapter.getCount(), is(0));
        // Execute
        controller.onSuccessLoadItemsById(incidList);
        ArrayAdapter<IncidenciaUser> adapterTest = controller.adapter;
        // Check
        assertThat(adapterTest.getCount(), is(1));
        assertThat(controller.getViewer().getViewInViewer().getAdapter(), CoreMatchers.<ListAdapter>is(adapterTest));
    }

    @Test
    public void testOnSuccessDealSelectedItem() throws Exception
    {
        controller.onSuccessSelectedItem(new Bundle());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ViewerIncidSeeForTest extends ViewerIncidListByComu {

        public ViewerIncidSeeForTest(ListView view, View emptyListView, Activity activity, ViewerIf parentViewer)
        {
            super(view, emptyListView, activity, parentViewer);
        }

        @Override
        public void replaceRootView(@NonNull Bundle bundle)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }
    }
}