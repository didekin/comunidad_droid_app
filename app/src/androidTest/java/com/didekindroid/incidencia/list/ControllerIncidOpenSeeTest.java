package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.ManagerIf;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.ViewerMock;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.ManagerMock.flagManageMockExecMethod;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.AFTER_seeIncidImportancia_EXEC;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.AFTER_seeIncidOpenList_EXEC;
import static com.didekindroid.incidencia.list.ReactorIncidSeeForTest.flagReactorMethodExec;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_REPLACED_VIEW;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 17:55
 */

public class ControllerIncidOpenSeeTest{

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    Activity activity;
    Incidencia incidencia;
    ManagerIf<IncidAndResolBundle> manager;
    ControllerIncidOpenSee controller;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        manager = new ManagerMock<IncidAndResolBundle>(activity){
            @Override
            public void replaceRootView(IncidAndResolBundle initParams)
            {
                super.replaceRootView(initParams);
                assertThat(initParams.hasResolucion(), is(true));
                assertThat(initParams.getIncidImportancia().getIncidencia(), is(incidencia));
                assertThat(initParams.getIncidImportancia().getImportancia(), is((short) 3));
            }
        };
        controller = new ControllerIncidOpenSee(new ViewerIncidSeeForTest(manager), new ReactorIncidSeeForTest(), TKhandler);
        incidencia = new Incidencia.IncidenciaBuilder().incidenciaId(1L)
                .comunidad(
                        new Comunidad.ComunidadBuilder().c_id(99L).build()
                )
                .userName(USER_JUAN.getUserName())
                .build();
    }

    //  ============================================================================================
    //    .................................... TESTS .................................
    //  ============================================================================================

    @Test
    public void loadIncidsByComu()
    {
        controller.loadIncidsByComu(incidencia.getComunidadId());
        assertThat(flagReactorMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_seeIncidOpenList_EXEC));
    }

    @Test
    public void dealWithIncidSelected() throws Exception
    {
        controller.dealWithIncidSelected(incidencia);
        assertThat(flagReactorMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_seeIncidImportancia_EXEC));
    }

    @Test
    public void processBackLoadIncidsByComu() throws Exception
    {
        IncidenciaUser incidenciaUser_1 = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_PEPE).build();
        IncidenciaUser incidenciaUser_2 = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(USER_JUAN).build();
        List<IncidenciaUser> incidOpenList = new ArrayList<>(2);
        incidOpenList.add(incidenciaUser_1);
        incidOpenList.add(incidenciaUser_2);

        // Preconditions
        assertThat(controller.adapter.getCount(), is(0));

        controller.processBackLoadIncidsByComu(incidOpenList);
        assertThat(controller.adapter.getCount(), is(2));
        assertThat(controller.viewer.getViewInViewer().getAdapter(), CoreMatchers.<ListAdapter>is(controller.adapter));
    }

    @Test
    public void processBackDealWithIncidencia() throws Exception
    {
        IncidImportancia incidImportancia = new IncidImportancia.IncidImportanciaBuilder(incidencia).importancia((short) 3).build();
        IncidAndResolBundle bundle = new IncidAndResolBundle(incidImportancia, true);
        controller.processBackDealWithIncidencia(bundle);
        assertThat(flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL), is(MANAGER_AFTER_REPLACED_VIEW));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ViewerIncidSeeForTest extends ViewerMock<ListView, IncidAndResolBundle> implements
            ViewerIncidSeeIf<IncidAndResolBundle> {

        protected ViewerIncidSeeForTest(ManagerIf<IncidAndResolBundle> manager)
        {
            super(manager);
            viewInViewer = new ListView(manager.getActivity());
        }

        @Override
        public void doIncidListView(Bundle savedState)
        {
        }

        @Override
        public ViewerWithSelectIf<ListView,IncidAndResolBundle> initSelectedIndex(Bundle savedState)
        {
            return null;
        }

        @Override
        public void saveSelectedIndex(Bundle savedState)
        {
        }
    }
}
