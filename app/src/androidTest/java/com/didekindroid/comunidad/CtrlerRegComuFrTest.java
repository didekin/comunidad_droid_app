package com.didekindroid.comunidad;

import android.os.Bundle;
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

import static com.didekindroid.comunidad.utils.ComuBundleKey.TIPO_VIA_ID;
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
 * Date: 08/05/17
 * Time: 17:11
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerRegComuFrTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerRegComuFr controller;
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
    public void test_LoadComunidadData() throws Exception
    {
        controller = new CtrlerRegComuFr(new ViewerRegComuFr(null, activityRule.getActivity(), null) {
            @Override
            void onSuccessLoadComunidad(Comunidad comunidad, Bundle savedState)
            {
                assertThat(comunidad, is(comunidad));
                assertThat(savedState.getLong(TIPO_VIA_ID.key), is(999L));
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
            }
        });

        Bundle bundle = new Bundle(1);
        bundle.putLong(TIPO_VIA_ID.key, 999L);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadComunidadData(comunidad.getC_Id(), bundle), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}