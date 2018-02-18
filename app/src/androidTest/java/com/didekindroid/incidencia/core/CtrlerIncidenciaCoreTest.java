package com.didekindroid.incidencia.core;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.MaybeObserverMock;
import com.didekindroid.lib_one.api.SingleObserverMock;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaByUsers;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.doIncidencia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_C;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 08/04/17
 * Time: 12:14
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidenciaCoreTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerIncidenciaCore controller;
    long incidenciaId;
    IncidImportancia incidImportancia;

    @Before
    public void setUp() throws Exception
    {
        incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
        incidenciaId = incidImportancia.getIncidencia().getIncidenciaId();
        controller = new CtrlerIncidenciaCore();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================= TESTS ===============================

    @Test
    public void testEraseIncidencia() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.eraseIncidencia(new DisposableSingleObserver<Integer>() {
                @Override
                public void onSuccess(Integer integer)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }, incidImportancia.getIncidencia()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    @Test
    public void testGetAmbitoIncidDesc() throws Exception
    {
        assertThat(controller.getAmbitoIncidDesc((short) 9), is("Buzones"));
    }

    @Test
    public void test_LoadItemsByEntitiyId() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadItemsByEntitiyId(
                    incidImportanciaByUsers(incidenciaId),
                    new SingleObserverMock<>(),
                    incidenciaId),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testModifyIncidImportancia() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyIncidImportancia(
                    new DisposableSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer integer)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            fail();
                        }
                    },
                    new IncidImportancia.IncidImportanciaBuilder(incidImportancia.getIncidencia())
                            .copyIncidImportancia(incidImportancia)
                            .importancia((short) 1)
                            .build()),
                    is(true)
            );
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testRegisterIncidencia() throws Exception
    {
        assertThat(controller.getSubscriptions().size(), is(0));

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(
                    controller.registerIncidImportancia(new DisposableSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer integer)
                        {
                            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_C), is(BEFORE_METHOD_EXEC));
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            fail();
                        }
                    }, doIncidImportancia()),
                    is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_C));
    }

    @Test
    public void testSeeResolucion() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.seeResolucion(new MaybeObserverMock<>(), incidenciaId), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //    .................................... HELPER METHODS .................................

    @NonNull
    private IncidImportancia doIncidImportancia()
    {
        return new IncidImportancia.IncidImportanciaBuilder(
                doIncidencia(
                        incidImportancia.getUserComu().getUsuario().getUserName(),
                        "Incidencia One",
                        incidImportancia.getUserComu().getComunidad().getC_Id(),
                        (short) 43
                )
        )
                .usuarioComunidad(incidImportancia.getUserComu())
                .importancia((short) 3)
                .build();
    }
}