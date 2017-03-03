package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.ControllerAbsTest;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekindroid.testutil.MockActivity;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 17:55
 */

public class ControllerIncidOpenSeeTest extends ControllerAbsTest<ControllerIncidOpenSee> {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    Activity activity;
    Incidencia incidencia;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        controller = new ControllerIncidOpenSee(new ViewerIncidSeeForTest(activity), new ReactorIncidSeeForTest());
        incidencia = new Incidencia.IncidenciaBuilder().incidenciaId(1L)
                .comunidad(
                        new Comunidad.ComunidadBuilder().c_id(99L).build()
                )
                .userName(USER_JUAN.getUserName())
                .build();
    }

    @After
    public void closeDown()
    {
        flagForExecution.set(0);
    }

    //  ============================================================================================
    //    .................................... TESTS .................................
    //  ============================================================================================

    //    ................................ CONTROLLER BASIC .................................

    @Override
    @Test
    public void testProcessReactorError()
    {
        controller.processReactorError(new Throwable());
        assertThat(flagForExecution.getAndSet(0), is(111));
    }

    //    ................................ ControllerIncidSeeIf .................................

    @Test
    public void loadIncidsByComu()
    {
        controller.loadIncidsByComu(incidencia.getComunidadId());
        assertThat(flagForExecution.getAndSet(0), is(110));
    }

    @Test
    public void dealWithIncidSelected() throws Exception
    {
        controller.dealWithIncidSelected(incidencia);
        assertThat(flagForExecution.getAndSet(0), is(112));
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
        assertThat(flagForExecution.getAndSet(0), is(113));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ViewerIncidSeeForTest extends ViewerDumbImp<ListView, IncidAndResolBundle> implements
            ViewerIncidSeeIf<IncidAndResolBundle> {

        protected ViewerIncidSeeForTest(Activity activity)
        {
            super(activity);
        }

        @Override
        public ListView doViewInViewer(Activity activity)
        {
            return new ListView(activity);
        }

        @Override
        public void doIncidListView(Bundle savedState)
        {
        }

        @Override
        public ViewerWithSelectIf initSelectedIndex(Bundle savedState)
        {
            return null;
        }

        @Override
        public void saveSelectedIndex(Bundle savedState)
        {
        }

        @Override  // Used in processBackDealWithIncidencia().
        public void replaceView(IncidAndResolBundle initParams)
        {
            assertThat(flagForExecution.getAndSet(113), is(0));
            assertThat(initParams.hasResolucion(), is(true));
            assertThat(initParams.getIncidImportancia().getIncidencia(), is(incidencia));
            assertThat(initParams.getIncidImportancia().getImportancia(), is((short) 3));
        }

        @Override // Used in testProcessReactorError().
        public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException e)
        {
            assertThat(flagForExecution.getAndSet(111), is(0));
            return null;
        }
    }

    class ReactorIncidSeeForTest implements ManagerIncidSeeIf.ReactorIncidSeeIf {

        @Override
        public boolean seeResolucion(ManagerIncidSeeIf.ControllerIncidSeeIf<Resolucion> controller, Incidencia incidencia)
        {
            return false;
        }

        @Override
        public boolean seeIncidClosedList(ManagerIncidSeeIf.ControllerIncidSeeIf controller, long comunidadId)
        {
            return false;
        }

        @Override // Used in loadIncidsByComu().
        public boolean seeIncidOpenList(ManagerIncidSeeIf.ControllerIncidSeeIf controller, long comunidadId)
        {
            assertThat(flagForExecution.getAndSet(110), is(0));
            return flagForExecution.get() == 110;
        }

        @Override  // Used in dealWithIncidSelected().
        public boolean seeIncidImportancia(ManagerIncidSeeIf.ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia)
        {
            assertThat(flagForExecution.getAndSet(112), is(0));
            return flagForExecution.get() == 112;
        }
    }
}
