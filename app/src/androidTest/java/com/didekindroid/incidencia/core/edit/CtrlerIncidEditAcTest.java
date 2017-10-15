package com.didekindroid.incidencia.core.edit;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;

import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetResolucionNoAdvances;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 08/04/17
 * Time: 12:14
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidEditAcTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerIncidEditAc controller;
    IncidAndResolBundle resolBundle;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                resolBundle = new IncidAndResolBundle(insertGetIncidImportancia(COMU_ESCORIAL_PEPE), false);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_RESOLUCION_BUNDLE.key, resolBundle);
            return intent;
        }
    };

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerIncidEditAc();
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
    public void testSeeResolucion() throws Exception
    {
        insertGetResolucionNoAdvances(resolBundle.getIncidImportancia());
        executeTest(new DisposableMaybeObserver<Resolucion>() {
            @Override
            public void onSuccess(@NonNull Resolucion resolucion)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                fail();
            }

            @Override
            public void onComplete()
            {
                fail();
            }
        });
    }

    @Test
    public void testSeeResolucion_NULL() throws Exception
    {
        executeTest(new DisposableMaybeObserver<Resolucion>() {
            @Override
            public void onSuccess(@NonNull Resolucion resolucion)
            {
                fail();
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                fail();
            }

            @Override
            public void onComplete()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        });
    }

    //    ============================= HELPERS ===============================

    void executeTest(DisposableMaybeObserver<Resolucion> observer)
    {
        Incidencia incidencia = resolBundle.getIncidImportancia().getIncidencia();
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.seeResolucion(observer, incidencia.getIncidenciaId()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}