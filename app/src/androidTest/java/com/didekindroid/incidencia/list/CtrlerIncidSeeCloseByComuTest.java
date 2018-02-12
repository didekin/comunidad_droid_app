package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.list.CtrlerIncidSeeCloseByComu.bundleWithResolucion;
import static com.didekindroid.incidencia.list.CtrlerIncidSeeCloseByComu.incidCloseList;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetDefaultResolucion;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 13:25
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidSeeCloseByComuTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    Resolucion resolucion;
    List<IncidenciaUser> incidList;
    Incidencia incidencia;
    IncidenciaUser incidenciaUser;
    CtrlerIncidSeeCloseByComu controller;
    Activity activity;
    UsuarioComunidad pepeUserComu;

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class, true, true) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
                pepeUserComu = userComuDaoRemote.seeUserComusByUser().get(0);
                resolucion = insertGetDefaultResolucion(pepeUserComu);
                assertThat(incidenciaDao.closeIncidencia(resolucion), is(2));
            } catch (UiException | InterruptedException | IOException e) {
                fail();
            }

            incidList = new ArrayList<>();
            incidencia = resolucion.getIncidencia();
            incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder(incidencia).usuario(pepeUserComu.getUsuario()).build();
            incidList.add(incidenciaUser);
        }
    };

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws IOException, UiException, InterruptedException
    {
        activity = activityRule.getActivity();
        controller = new CtrlerIncidSeeCloseByComu();
        assertThat(controller, notNullValue());
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    // ......................... OBSERVABLES .............................

    @Test
    public void testResolucion()
    {
        bundleWithResolucion(resolucion.getIncidencia()).test().assertOf(bundleTestObserver -> {
            Bundle bundleIn = bundleTestObserver.values().get(0);
            checkBundle(bundleIn);
        });
    }


    @Test
    public void testIncidCloseList()
    {
        incidCloseList(resolucion.getIncidencia().getComunidadId()).test().assertValue(incidList);
    }

    /* ............................ INSTANCE METHODS ...............................*/

    @Test
    public void testLoadItemsByEntitiyId()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<IncidenciaUser>>() {
                @Override
                public void onSuccess(List<IncidenciaUser> incidenciaUsers)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, pepeUserComu.getComunidad().getC_Id()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testSelectItem()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.selectItem(new DisposableSingleObserver<Bundle>() {
                @Override
                public void onSuccess(Bundle bundle)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, incidenciaUser), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    void checkBundle(Bundle bundleIn)
    {
        assertThat(bundleIn.getSerializable(INCID_RESOLUCION_OBJECT.key), CoreMatchers.is(resolucion));
    }
}