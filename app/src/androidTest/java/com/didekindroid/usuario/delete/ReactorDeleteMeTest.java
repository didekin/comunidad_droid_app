package com.didekindroid.usuario.delete;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.delete.ControllerDeleteMe.ReactorDeleteMe.DeleteMeSingleObserver;
import com.didekinlib.http.ErrorBean;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.delete.ControllerDeleteMe.ReactorDeleteMe.deleteReactor;
import static com.didekindroid.usuario.delete.ControllerDeleteMe.ReactorDeleteMe.getDeleteMeSingle;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/01/17
 * Time: 18:41
 */
@RunWith(AndroidJUnit4.class)
public class ReactorDeleteMeTest {

    AtomicInteger testCounter = new AtomicInteger(0);

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        TKhandler.updateIsGcmTokenSentServer(true);
        // PRECONDITIONS.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get().getValue(), notNullValue());
    }

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    //  ====================================================================================
    //    .......................... OBSERVABLES .................................
    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testGetDeleteMeSingle_1() throws Exception
    {
        getDeleteMeSingle().test().assertResult(true);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.isGcmTokenSentServer(), is(false));
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onError method: we test that the testCounter increases en 11;
     */
    @Test
    public void testGetDeleteMeSingle_2() throws UiException
    {
        Single.<Boolean>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new DeleteMeSingleObserver(doMockController()));
        assertThat(testCounter.get(), is(11));
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onSuccess method: we test that the testCounter increases in 111.
     */
    @Test
    public void testGetDeleteMeSingle_3() throws UiException
    {
        Single.just(true).subscribeWith(new DeleteMeSingleObserver(doMockController()));
        assertThat(testCounter.get(), is(111));
    }


    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution (everything runs in the test runner thread):
     * 1. We use RxJavaPlugins to replace io scheduler;
     * 2. We use RxAndroidPlugins to replace Android main thread scheduler.
     *
     * We delete successfully a user: testCounter OK.
     * We delete unsuccessfully a previously deleted user: testCounter OK.
     */
    @Test
    public void testUpdateTkAndCacheFromUser() throws IOException, UiException
    {

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(deleteReactor.deleteMeInRemote(doMockController()), is(true));
            assertThat(testCounter.getAndSet(0), is(111));
            assertThat(deleteReactor.deleteMeInRemote(doMockController()), is(true));
            assertThat(testCounter.getAndSet(111), is(11));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ControllerDeleteMeIf doMockController(){

        return new ControllerDeleteMeIf() {

            ManagerIf.ControllerIf controllerAbs = new ControllerAbs() {
                @Override
                public ManagerIf.ViewerIf getViewer()
                {
                    return null;
                }
            };

            @Override
            public boolean unregisterUser()
            {
                return false;
            }

            @Override
            public void processBackDeleteMeRemote(boolean isDeleted)
            {
                assertThat(isDeleted, is(true));
                assertThat(ReactorDeleteMeTest.this.testCounter.getAndSet(111), is(0));
            }

            @Override
            public CompositeDisposable getSubscriptions()
            {
                return controllerAbs.getSubscriptions();
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

            @Override
            public void processReactorError(Throwable e)
            {
                assertThat(ReactorDeleteMeTest.this.testCounter.getAndSet(11), is(0));
            }
        };
    }

}