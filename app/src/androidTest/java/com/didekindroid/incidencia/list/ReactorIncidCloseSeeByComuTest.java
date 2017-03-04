package com.didekindroid.incidencia.list;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf.ControllerIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.IncidListObserver;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
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
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidCloseList;
import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.list.ReactorIncidSee.resolucion;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
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

    UsuarioComunidad pepeUserComu;
    Resolucion resolucion;
    List<IncidenciaUser> incidList;
    AtomicInteger counter = new AtomicInteger(0);

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
            assertThat(incidSeeReactor.seeResolucion(doController(), resolucion.getIncidencia()), is(true));
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
            assertThat(incidSeeReactor.seeIncidClosedList(doController(), resolucion.getComunidadId()), is(true));
        } finally {
            reset();
        }
    }

    // .................................... SUBSCRIBERS ............................................

    // Maybe without item.
    @Test
    public void testIncidCloseListObserver()
    {
        Maybe.<List<IncidenciaUser>>fromAction(new Action() {
            @Override
            public void run() throws Exception
            {
                assertThat(counter.getAndAdd(11), is(0));
            }
        }).subscribeWith(new IncidListObserver(doController()){
            @Override
            public void onComplete()
            {
                super.onComplete();
                assertThat(counter.get(), is(11));
            }
        });
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ControllerIncidSeeIf<Resolucion> doController()
    {
        return new ControllerIncidSeeIf<Resolucion>() {

            ControllerIf controllerAbs = new ControllerAbs() {
                @Override
                public ViewerFirebaseTokenIf getViewer()
                {
                    return null;
                }
            };

            @Override
            public void loadIncidsByComu(long comunidadId)
            {
            }

            @Override
            public void processBackLoadIncidsByComu(List<IncidenciaUser> incidList)
            {
                Timber.d("processBackLoadIncidsByComu()");
                ArrayList<IncidenciaUser> list = (ArrayList<IncidenciaUser>) incidList;
                assertThat(list.size(), is(1));
                assertThat(list.get(0).getIncidencia(), is(resolucion.getIncidencia()));
            }

            @Override
            public void dealWithIncidSelected(Incidencia incidencia)
            {
            }

            @Override
            public void processBackDealWithIncidencia(Resolucion resolucion)
            {
                Timber.d("processBackDealWithIncidencia()");
                assertThat(resolucion, is(ReactorIncidCloseSeeByComuTest.this.resolucion));
            }

            @Override
            public CompositeDisposable getSubscriptions()
            {
                return controllerAbs.getSubscriptions();
            }

            @Override
            public void processReactorError(Throwable e)
            {
                controllerAbs.processReactorError(e);
            }

            @Override
            public int clearSubscriptions()
            {
                return controllerAbs.clearSubscriptions();
            }

            @Override
            public ViewerFirebaseTokenIf getViewer()
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