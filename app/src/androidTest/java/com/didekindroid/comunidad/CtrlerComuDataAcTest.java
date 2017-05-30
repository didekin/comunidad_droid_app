package com.didekindroid.comunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpWithTkGetComu;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 09/05/17
 * Time: 12:50
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerComuDataAcTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    CtrlerComuDataAc controller;
    Comunidad comunidad;

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                comunidad = signUpWithTkGetComu(COMU_ESCORIAL_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    @After
    public void clearUp() throws UiException
    {
        controller.clearSubscriptions();
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_ModifyComunidadData() throws Exception
    {
        controller = new CtrlerComuDataAc(new ViewerComuDataAc(null, activityRule.getActivity()){
            @Override
            void onSuccessModifyComunidad()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        });

        Comunidad newComunidad = new Comunidad.ComunidadBuilder()
                .copyComunidadNonNullValues(comunidad).nombreVia("nuevo_nombre_via").build();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.modifyComunidadData(, newComunidad), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}