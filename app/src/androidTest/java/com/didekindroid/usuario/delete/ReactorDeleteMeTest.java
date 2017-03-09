package com.didekindroid.usuario.delete;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.ViewerMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
import com.didekindroid.usuario.delete.ReactorDeleteMe.DeleteMeSingleObserver;
import com.didekinlib.http.ErrorBean;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.ViewerMock.flagViewerMockMethodExec;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.VIEWER_AFTER_ERROR_CONTROL;
import static com.didekindroid.testutil.ConstantExecution.VIEWER_FLAG_INITIAL;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.delete.ReactorDeleteMe.deleteReactor;
import static com.didekindroid.usuario.delete.ReactorDeleteMe.getDeleteMeSingle;
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

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        // PRECONDITIONS.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get().getValue(), notNullValue());
    }

    //  ====================================================================================
    //    .......................... OBSERVABLES .................................
    //  ====================================================================================

    @Test
    public void testGetDeleteMeSingle_1() throws Exception
    {
        getDeleteMeSingle().test().assertResult(true);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    @Test
    public void testGetDeleteMeSingle_2() throws UiException
    {
        // Test onError.
        Single.<Boolean>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new DeleteMeSingleObserver(new ControllerDeleteMeForTest()));
        assertThat(flagViewerMockMethodExec.getAndSet(VIEWER_FLAG_INITIAL), is(VIEWER_AFTER_ERROR_CONTROL));
    }

    @Test
    public void testGetDeleteMeSingle_3() throws UiException
    {
        // Test onSuccess.
        Single.just(true).subscribeWith(new DeleteMeSingleObserver(new ControllerDeleteMeForTest()));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }


    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution (everything runs in the test runner thread):
     * 1. We use RxJavaPlugins to replace io scheduler;
     * 2. We use RxAndroidPlugins to replace Android main thread scheduler.
     * <p>
     * We delete successfully a user.
     * We delete unsuccessfully a previously deleted user.
     */
    @Test
    public void testUpdateTkAndCacheFromUser() throws IOException, UiException
    {

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(deleteReactor.deleteMeInRemote(new ControllerDeleteMeForTest()), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
            // Throw an UiException.
            assertThat(deleteReactor.deleteMeInRemote(new ControllerDeleteMeForTest()), is(true));
            assertThat(flagViewerMockMethodExec.getAndSet(VIEWER_FLAG_INITIAL), is(VIEWER_AFTER_ERROR_CONTROL));
        } finally {
            reset();
        }
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class ControllerDeleteMeForTest extends ControllerIdentityAbs implements ControllerDeleteMeIf {

        @Override
        public boolean unregisterUser()
        {
            return false;
        }

        @Override
        public void processBackDeleteMeRemote(boolean isDeleted)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            assertThat(isDeleted, is(true));
        }

        @Override
        public ManagerIncidSeeIf.ViewerIf<View,Object> getViewer()
        {
            return new ViewerMock<>(new ManagerMock<>(activityRule.getActivity()));
        }
    }
}