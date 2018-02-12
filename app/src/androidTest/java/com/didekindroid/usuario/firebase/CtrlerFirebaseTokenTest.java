package com.didekindroid.usuario.firebase;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuario.firebase.InstanceIdService.ServiceDisposableSingleObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.firebase.CtrlerFirebaseToken.updatedGcmTkSingle;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerFirebaseTokenTest {

    static final AtomicReference<String> flagControl = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerFirebaseToken controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        controller = new CtrlerFirebaseToken();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ................................ OBSERVABLES/SUBSCRIBERS .................................

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUpdatedGcmTkSingle() throws Exception
    {
        updatedGcmTkSingle().test().assertResult(1);
    }

    //    ................................. INSTANCE METHODS ...............................

    @Test
    public void test_IsGcmTokenSentServer() throws Exception
    {
        controller.getIdentityCacher().updateIsRegistered(true);

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void test_UpdateIsGcmTokenSentServer() throws Exception
    {
        controller.getIdentityCacher().updateIsRegistered(true);
        assertThat(controller.isGcmTokenSentServer(), is(false));

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void test_CheckGcmTokenAsync() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();

            // Preconditions.
            assertThat(controller.getIdentityCacher().isRegisteredUser(), is(true));
            controller.updateIsGcmTokenSentServer(true);
            /* Execute. FALSE: no update because is already updated.*/
            assertThat(controller.checkGcmTokenAsync(new TestDisposableSingleObserver()), is(false));
            assertThat(controller.getSubscriptions().size(), is(0));
            // Mantains status.
            assertThat(flagControl.get(), is(BEFORE_METHOD_EXEC));
            assertThat(controller.isGcmTokenSentServer(), is(true));

            // Preconditions.
            controller.updateIsGcmTokenSentServer(false);
            /* Execute.*/
            assertThat(controller.checkGcmTokenAsync(new TestDisposableSingleObserver()), is(true));
            assertThat(controller.getSubscriptions().size(), is(1));
            // Change status.
            assertThat(flagControl.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));

        } finally {
            resetAllSchedulers();
        }
    }

    @Test
    public void test_CheckGcmTokenSync_1() throws Exception
    {
        // Preconditions.
        assertThat(controller.getIdentityCacher().isRegisteredUser(), is(true));
        controller.updateIsGcmTokenSentServer(true);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(true));
        // The token is updated: controller open subscription.
        assertThat(controller.getSubscriptions().size(), is(1));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(false));
        // NO increase in subscriptions.
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(controller.isGcmTokenSentServer(), is(false));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(true));
        assertThat(controller.getSubscriptions().size(), is(2));
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }

    @Test
    public void test_CheckGcmTokenSync_2() throws Exception
    {
        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.getSubscriptions().size(), is(0));

        controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller));
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }

    // ==============================  HELPERS  ==================================

    static class TestDisposableSingleObserver extends DisposableSingleObserver<Integer> {
        @Override
        public void onSuccess(Integer integer)
        {
            assertThat(flagControl.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onError(Throwable e)
        {
            fail();
        }
    }
}