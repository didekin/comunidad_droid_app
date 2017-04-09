package com.didekindroid.incidencia.core.edit;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.testutils.IncidDataTestUtils.insertGetIncidImportancia;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 06/04/17
 * Time: 10:36
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIncidEditMaxFrTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerIncidEditMaxFr controller;
    UsuarioComunidad pepeUserComu;
    IncidImportancia incidImportancia;

    @Rule
    public IntentsTestRule<IncidEditAc> activityRule = new IntentsTestRule<IncidEditAc>(IncidEditAc.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                incidImportancia = insertGetIncidImportancia(COMU_ESCORIAL_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
            Intent intent = new Intent();
            intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
            intent.putExtra(INCID_RESOLUCION_FLAG.key, false);
            return intent;
        }
    };

    @Before
    public void setUp()
    {
        Activity activity = activityRule.getActivity();
        controller = new CtrlerIncidEditMaxFr(new ViewerIncidEditMaxFr(null, activity, null, false) {
            @Override
            void onSuccessModifyIncidImportancia(int rowInserted)
            {
                assertThat(rowInserted >= 1, is(true));
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }

            @Override
            void onSuccessEraseIncidencia(int rowsDeleted)
            {
                assertThat(rowsDeleted, is(1));
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
            }
        });
    }

    @After
    public void clean() throws UiException
    {
        controller.clearSubscriptions();
        cleanOptions(CLEAN_PEPE);
    }

    //    ============================= TESTS ===============================

    @Test
    public void testModifyIncidImportancia() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyIncidImportancia(
                    new IncidImportancia.IncidImportanciaBuilder(incidImportancia.getIncidencia())
                            .copyIncidImportancia(incidImportancia)
                            .importancia((short) 1)
                            .build()),
                    is(true)
            );
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }


    @Test
    public void testEraseIncidencia() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.eraseIncidencia(incidImportancia.getIncidencia()), is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }
}