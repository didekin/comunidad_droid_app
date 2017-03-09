package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.ManagerIf;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.ViewerMock;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
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

import static com.didekindroid.ManagerMock.flagManageMockExecMethod;
import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.AFTER_seeIncidClosedList_EXEC;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.AFTER_seeResolucioin_EXEC;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.flagReactorMethodExec;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_REPLACED_VIEW;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 13:25
 */
@RunWith(AndroidJUnit4.class)
public class ControllerIncidCloseSeeTest {

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    UsuarioComunidad pepeUserComu;
    Resolucion resolucion;
    List<IncidenciaUser> incidList;
    Incidencia incidencia;
    Activity activity;
    ManagerIf<Bundle> manager;
    ControllerIncidCloseSee controller;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws IOException, UiException, InterruptedException
    {
        activity = activityRule.getActivity();
        manager = new ManagerMock<Bundle>(activity){
            @Override
            public void replaceRootView(Bundle bundle)
            {
                super.replaceRootView(bundle);
                assertThat(bundle.getSerializable(INCIDENCIA_OBJECT.key), CoreMatchers.<Serializable>is(controller.atomicIncidencia.get()));
                assertThat(bundle.getSerializable(INCID_RESOLUCION_OBJECT.key), CoreMatchers.<Serializable>is(resolucion));
            }
        };
        controller = new ControllerIncidCloseSee(new ViewerIncidSeeForTest(manager), new ReactorIncidSeeForTest(), TKhandler);

        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
        resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));

        incidList = new ArrayList<>();
        incidencia = resolucion.getIncidencia();
        incidList.add(new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(pepeUserComu.getUsuario()).build());
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    /* ............................ TESTS ...............................*/

    @Test
    public void testLoadIncidsByComu()
    {
        controller.loadIncidsByComu(9L);
        assertThat(flagReactorMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_seeIncidClosedList_EXEC));
    }

    @Test
    public void testDealWithIncidSelected()
    {
        controller.dealWithIncidSelected(incidencia);
        assertThat(controller.atomicIncidencia.get(), notNullValue());
        assertThat(flagReactorMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_seeResolucioin_EXEC));
    }

    @Test
    public void testProcessBackLoadIncidsByComu() throws Exception
    {
        // Precondition:
        assertThat(controller.adapter.getCount(), is(0));
        // Execute
        controller.processBackLoadIncidsByComu(incidList);
        ArrayAdapter<IncidenciaUser> adapterTest = controller.adapter;
        // Check
        assertThat(adapterTest.getCount(), is(1));
        assertThat(controller.viewer.getViewInViewer().getAdapter(), CoreMatchers.<ListAdapter>is(adapterTest));
    }

    @Test
    public void testProcessBackDealWithIncidencia() throws Exception
    {
        // Incializamos atomicIncidencia, que se haría previamente en controller.dealWithIncidSelected(incidencia).
        controller.atomicIncidencia.compareAndSet(null, incidencia);
        controller.processBackDealWithIncidencia(resolucion);
        assertThat(flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL), is(MANAGER_AFTER_REPLACED_VIEW));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ViewerIncidSeeForTest extends ViewerMock<ListView, Bundle> implements
            ViewerIncidSeeIf<Bundle> {

        protected ViewerIncidSeeForTest(ManagerIf<Bundle> manager)
        {
            super(manager);
            viewInViewer = new ListView(manager.getActivity());
        }

        @Override
        public void doIncidListView(Bundle savedState)
        {
        }

        @Override
        public ViewerWithSelectIf<ListView,Bundle> initSelectedIndex(Bundle savedState)
        {
            return null;
        }

        @Override
        public void saveSelectedIndex(Bundle savedState)
        {
        }
    }
}