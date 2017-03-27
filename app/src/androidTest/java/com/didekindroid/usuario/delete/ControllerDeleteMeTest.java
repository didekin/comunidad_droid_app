package com.didekindroid.usuario.delete;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.RootViewReplacerIf;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.delete.CtrlerDeleteMe.getDeleteMeSingle;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 12:47
 */
@RunWith(AndroidJUnit4.class)
public class ControllerDeleteMeTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerDeleteMeIf controller;

    @Before
    public void setUp() throws IOException, UiException
    {
        controller = new CtrlerDeleteMe(activityRule.getActivity());
        signUpAndUpdateTk(COMU_REAL_DROID);
        // PRECONDITIONS.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getTokenCache().get().getValue(), notNullValue());
    }

    // ................................. OBSERVABLES ...............................

    @Test
    public void testGetDeleteMeSingle_1() throws Exception
    {
        getDeleteMeSingle().test().assertResult(true);
        assertThat(TKhandler.getTokenCache().get(), nullValue());
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    // ............................ SUBSCRIBERS ..................................

    @Test
    public void testSubscriberOnError() throws UiException
    {
        Single.<Boolean>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new CtrlerDeleteMe.DeleteMeSingleObserver(controller){
                    @Override
                    public void onError(Throwable e)
                    {
                        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
                    }
                });

        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
    }

    // .............................. INSTANCE METHODS .............................

    @Test
    public void testDeleteMeRemote() throws Exception
    {
        assertThat(controller.deleteMeRemote(), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testOnSuccessDeleteMeRemote() throws Exception
    {
        controller.onSuccessDeleteMeRemote(true);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class RootViewReplacerForTest implements RootViewReplacerIf{

        @Override
        public void replaceRootView(@NonNull Bundle bundle)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }
    }
}