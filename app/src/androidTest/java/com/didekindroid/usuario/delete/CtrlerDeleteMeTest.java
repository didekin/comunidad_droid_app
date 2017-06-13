package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.delete.CtrlerDeleteMe.getDeleteMeSingle;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 12:47
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerDeleteMeTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<DeleteMeAc> activityRule = new ActivityTestRule<DeleteMeAc>(DeleteMeAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_REAL_DROID);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    CtrlerDeleteMe controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        Activity activity = activityRule.getActivity();
        controller = new CtrlerDeleteMe();
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @After
    public void cleanUp()
    {
        assertThat(controller.clearSubscriptions(), is(0));
    }

    // ................................. OBSERVABLES ...............................

    @Test
    public void testGetDeleteMeSingle() throws Exception
    {
        getDeleteMeSingle().test().assertResult(true);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    // .............................. INSTANCE METHODS .............................

    @Test
    public void testDeleteMeRemote() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            Timber.d("checkSpinnerCtrlerLoadItems(), Thread: %s", Thread.currentThread().getName());
            assertThat(controller.deleteMeRemote(new DisposableSingleObserver<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean)
                {
                    assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                }

                @Override
                public void onError(Throwable e)
                {
                    fail();
                }
            }), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

}