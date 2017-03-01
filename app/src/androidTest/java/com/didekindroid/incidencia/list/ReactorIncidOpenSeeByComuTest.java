package com.didekindroid.incidencia.list;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf;
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

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.list.ReactorIncidSee.incidImportancia;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidOpenList;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.INCID_DEFAULT_DESC;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidenciaUser;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.makeAndRegIncidImportancia;
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

    UsuarioComunidad pepeUserComu;

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
            assertThat(incidSeeReactor.seeIncidOpenList(doController(), pepeUserComu.getComunidad().getC_Id()), is(true));
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
            assertThat(incidSeeReactor.seeIncidImportancia(doController(), incidencia), is(true));
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
            assertThat(incidSeeReactor.seeIncidOpenList(doController(), 999L), is(true));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ControllerIncidSeeIf<IncidAndResolBundle> doController()
    {
        return new ControllerIncidSeeIf<IncidAndResolBundle>() {

            ManagerIf.ControllerIf controllerAbs = new ControllerAbs() {
                @Override
                public ManagerIf.ViewerIf getViewer()
                {
                    return null;
                }
            };

            @Override
            public void loadIncidsByComu(long comunidadId)
            {
            }

            @Override
            public void processBackLoadIncidsByComu(List incidList)
            {
                Timber.d("====================== processBackLoadIncidsByComu ===================");
                assertThat(incidList.size(), is(1));
            }

            @Override
            public void dealWithIncidSelected(Incidencia incidencia)
            {
            }

            @Override
            public void processBackDealWithIncidencia(IncidAndResolBundle incidResolBundle)
            {
                Timber.d("====================== processBackDealWithIncidencia ==================");
                assertThat(incidResolBundle.hasResolucion(), is(false));
            }

            @Override
            public CompositeDisposable getSubscriptions()
            {
                return controllerAbs.getSubscriptions();
            }

            @Override
            public void processReactorError(Throwable e)
            {
               // testSeeIncidOpenList_ERROR()
                UiException ue = (UiException) e;
                assertThat(ue.getErrorBean().getMessage(), is(USERCOMU_WRONG_INIT.getHttpMessage()));
            }

            @Override
            public int clearSubscriptions()
            {
                return controllerAbs.clearSubscriptions();
            }

            @Override
            public ManagerIf.ViewerIf getViewer()
            {
                return controllerAbs.getViewer();
            }

            @Override
            public boolean isRegisteredUser()
            {
                return controllerAbs.isRegisteredUser();
            }
        };
    }

}