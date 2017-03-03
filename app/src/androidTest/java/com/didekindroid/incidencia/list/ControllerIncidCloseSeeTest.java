package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.didekindroid.ControllerAbsTest;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekindroid.testutil.MockActivity;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
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
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
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
public class ControllerIncidCloseSeeTest extends ControllerAbsTest<ControllerIncidCloseSee> {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    UsuarioComunidad pepeUserComu;
    Resolucion resolucion;
    List<IncidenciaUser> incidList;
    Incidencia incidencia;
    Activity activity;

    @Before
    public void setUp() throws IOException, UiException, InterruptedException
    {
        // Controller in superclass.
        activity = activityRule.getActivity();
        controller = new ControllerIncidCloseSee(new ViewerIncidSeeForTest(activity), new ReactorIncidSeeForTest());

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
        assertThat(flagForExecution.getAndSet(0), is(907));
    }

    @Test
    public void testDealWithIncidSelected()
    {
        controller.dealWithIncidSelected(incidencia);
        assertThat(controller.atomicIncidencia.get(), notNullValue());
        assertThat(flagForExecution.getAndSet(0), is(53));
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
        assertThat(flagForExecution.getAndSet(0), is(37));
    }

    @Test
    @Override
    public void testProcessReactorError()
    {
        controller.processReactorError(new Throwable());
        assertThat(flagForExecution.getAndSet(0), is(773));
    }


    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ViewerIncidSeeForTest extends ViewerDumbImp<ListView, Bundle> implements
            ViewerIncidSeeIf<Bundle> {

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

        @Override // Used in testProcessReactorError().
        public ActionForUiExceptionIf processControllerError(UiException ui)
        {
            Timber.d(" ===================== processControllerError() ==========================");
            assertThat(flagForExecution.getAndSet(773), is(0));
            return null;
        }

        @Override  // Used in testProcessBackDealWithIncidencia()
        public void replaceView(Bundle bundle)
        {
            Timber.d(" ===================== replaceRootView() ==========================");
            assertThat(flagForExecution.getAndSet(37), is(0));
            assertThat(bundle.getSerializable(INCIDENCIA_OBJECT.key), CoreMatchers.<Serializable>is(controller.atomicIncidencia.get()));
            assertThat(bundle.getSerializable(INCID_RESOLUCION_OBJECT.key), CoreMatchers.<Serializable>is(resolucion));
        }
    }

    class ReactorIncidSeeForTest implements ManagerIncidSeeIf.ReactorIncidSeeIf {

        @Override  // User in testDealWithIncidSelected().
        public boolean seeResolucion(ControllerIncidSeeIf<Resolucion> controller, Incidencia incidencia)
        {
            assertThat(flagForExecution.getAndSet(53), is(0));
            return flagForExecution.get() == 53;
        }

        @Override  // Used in testLoadIncidsByComu().
        public boolean seeIncidClosedList(ControllerIncidSeeIf controller, long comunidadId)
        {
            assertThat(flagForExecution.getAndSet(907), is(0));
            return flagForExecution.get() == 907;
        }

        @Override // Used in ControllerIncidOpenSeeTest.loadIncidsByComu().
        public boolean seeIncidOpenList(ControllerIncidSeeIf controller, long comunidadId)
        {
            return false;
        }

        @Override  // Used in dealWithIncidSelected().
        public boolean seeIncidImportancia(ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia)
        {
            return false;
        }
    }
}