package com.didekindroid.lib_one.usuario.notification;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.usuario.notification.InstanceIdService.ServiceDisposableSingleObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.notification.CtrlerNotifyToken.updatedGcmTkSingle;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerNotifyTokenTest {

    static final AtomicReference<String> flagControl = new AtomicReference<>(BEFORE_METHOD_EXEC);

    CtrlerNotifyToken controller;
    private IdentityCacherIf identityCacher;

    @Before
    public void setUp() throws IOException, UiException
    {
        regUserComuWithTkCache(comu_real_rodrigo);
        controller = new CtrlerNotifyToken();
        identityCacher = controller.getTkCacher();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(user_crodrigo);
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
    public void test_CheckGcmTokenAsync() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();

            /* Preconditions.*/
            assertThat(identityCacher.isRegisteredUser(), is(true));
            identityCacher.updateIsGcmTokenSentServer(true);
            /* Execute. FALSE: no update because is already updated.*/
            assertThat(controller.checkGcmTokenAsync(new TestDisposableSingleObserver()), is(false));
            assertThat(controller.getSubscriptions().size(), is(0));
            // Mantains status.
            assertThat(flagControl.get(), is(BEFORE_METHOD_EXEC));
            assertThat(identityCacher.isGcmTokenSentServer(), is(true));

            // Preconditions.
            identityCacher.updateIsGcmTokenSentServer(false);
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
        assertThat(identityCacher.isRegisteredUser(), is(true));
        identityCacher.updateIsGcmTokenSentServer(true);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(true));
        // The token is updated: controller open subscription.
        assertThat(controller.getSubscriptions().size(), is(1));

        // Preconditions.
        identityCacher.updateIsRegistered(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(false));
        // NO increase in subscriptions.
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(identityCacher.isGcmTokenSentServer(), is(false));

        // Preconditions.
        identityCacher.updateIsRegistered(true);
        identityCacher.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller)), is(true));
        assertThat(controller.getSubscriptions().size(), is(2));
        assertThat(identityCacher.isGcmTokenSentServer(), is(true));
    }

    @Test
    public void test_CheckGcmTokenSync_2() throws Exception
    {
        // Preconditions.
        identityCacher.updateIsRegistered(true);
        identityCacher.updateIsGcmTokenSentServer(false);
        assertThat(controller.getSubscriptions().size(), is(0));

        controller.checkGcmTokenSync(new ServiceDisposableSingleObserver(controller));
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(identityCacher.isGcmTokenSentServer(), is(true));
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