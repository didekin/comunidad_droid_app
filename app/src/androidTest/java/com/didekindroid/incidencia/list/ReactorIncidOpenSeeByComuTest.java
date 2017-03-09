package com.didekindroid.incidencia.list;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.incidencia.list.ReactorIncidSee.incidImportancia;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidOpenList;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeAndRegIncidImportancia;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuariocomunidad.UsuarioComunidadExceptionMsg.USERCOMU_WRONG_INIT;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/02/17
 * Time: 13:21
 */
public class ReactorIncidOpenSeeByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    UsuarioComunidad pepeUserComu;
    final static String AFTER_backDealWithIncidencia_EXEC = "executed after processBackDealWithIncidencia method";

    @Before
    public void setUp() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
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
    public void testIncidOpenList() throws UiException
    {
        assertThat(makeAndRegIncidImportancia(pepeUserComu, (short) 1), is(2));

        incidOpenList(pepeUserComu.getComunidad().getC_Id()).test().assertOf(new Consumer<TestObserver<List<IncidenciaUser>>>() {
            @Override
            public void accept(TestObserver<List<IncidenciaUser>> testObserver) throws Exception
            {
                List<IncidenciaUser> list = testObserver.values().get(0);
                assertThat(list.size(), is(1));
                assertThat(list.get(0).getIncidencia().getDescripcion(), is(INCID_DEFAULT_DESC));
                assertThat(list.get(0).getIncidencia().getImportanciaAvg(), is(1f));
            }
        });
    }

    @Test
    public void testIncidImportancia() throws UiException
    {
        final Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();

        incidImportancia(incidencia).test().assertOf(new Consumer<TestObserver<IncidAndResolBundle>>() {
            @Override
            public void accept(TestObserver<IncidAndResolBundle> testObserver) throws Exception
            {
                IncidAndResolBundle bundle = testObserver.values().get(0);
                assertThat(bundle.hasResolucion(), is(false));
                assertThat(bundle.getIncidImportancia().getIncidencia(), is(incidencia));
            }
        });
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void testSeeIncidOpenList() throws Exception
    {
        assertThat(makeAndRegIncidImportancia(pepeUserComu, (short) 1), is(2));

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(incidSeeReactor.seeIncidOpenList(new ControllerIncidCloseForTest(), pepeUserComu.getComunidad().getC_Id()), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
        } finally {
            reset();
        }
    }

    @Test
    public void testSeeIncidImportancia() throws Exception
    {
        final Incidencia incidencia = insertGetIncidenciaUser(pepeUserComu, 1).getIncidencia();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(incidSeeReactor.seeIncidImportancia(new ControllerIncidCloseForTest(), incidencia), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_backDealWithIncidencia_EXEC));
        } finally {
            reset();
        }
    }

    @Test
    public void testSeeIncidOpenList_ERROR() throws Exception
    {
        assertThat(makeAndRegIncidImportancia(pepeUserComu, (short) 1), is(2));

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(incidSeeReactor.seeIncidOpenList(new ControllerIncidCloseForTest(), 999L), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ControllerIncidCloseForTest extends ControllerIdentityAbs implements ControllerIncidSeeIf<IncidAndResolBundle>{

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
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            assertThat(incidList.size(), is(1));
        }

        @Override
        public void dealWithIncidSelected(Incidencia incidencia)
        {
        }

        @Override
        public void processBackDealWithIncidencia(IncidAndResolBundle itemBack)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_backDealWithIncidencia_EXEC), is(BEFORE_METHOD_EXEC));
            assertThat(itemBack.hasResolucion(), is(false));
        }

        @Override  // testSeeIncidOpenList_ERROR()
        public void processReactorError(Throwable e)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
            UiException ue = (UiException) e;
            assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
        }
    }
}