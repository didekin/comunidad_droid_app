package com.didekindroid.incidencia.list;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidCloseList;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.list.ReactorIncidSee.resolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 13:34
 */
@RunWith(AndroidJUnit4.class)
public class ReactorIncidCloseSeeByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    UsuarioComunidad pepeUserComu;
    Resolucion resolucion;
    List<IncidenciaUser> incidList;

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    @Before
    public void setUp() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
        resolucion = insertGetDefaultResolucion(pepeUserComu);
        assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));

        incidList = new ArrayList<>();
        Incidencia incidencia = resolucion.getIncidencia();
        incidList.add(new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(pepeUserComu.getUsuario()).build());
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    @Test
    public void testResolucion()
    {
        resolucion(resolucion.getIncidencia()).test().assertResult(resolucion);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIncidCloseList()
    {
        incidCloseList(resolucion.getIncidencia().getComunidadId()).test().assertResult(incidList);
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void testSeeResolucion() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(incidSeeReactor.seeResolucion(new ControllerIncidSeeForTest(), resolucion.getIncidencia()), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
        } finally {
            reset();
        }
    }

    @Test
    public void testSeeIncidCloseList() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(incidSeeReactor.seeIncidClosedList(new ControllerIncidSeeForTest(), resolucion.getComunidadId()), is(true));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ControllerIncidSeeForTest extends ControllerIdentityAbs implements ControllerIncidSeeIf<Resolucion>{

        @Override
        public ManagerIncidSeeIf.ViewerIf getViewer()
        {
            return null;
        }

        @Override
        public void loadIncidsByComu(long comunidadId)
        {
        }

        @Override
        public void processBackLoadIncidsByComu(List<IncidenciaUser> incidList)
        {
        }

        @Override
        public void dealWithIncidSelected(Incidencia incidencia)
        {
        }

        @Override  // Used in testSeeResolucion().
        public void processBackDealWithIncidencia(Resolucion itemBack)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            assertThat(resolucion, is(ReactorIncidCloseSeeByComuTest.this.resolucion));
        }
    }
}