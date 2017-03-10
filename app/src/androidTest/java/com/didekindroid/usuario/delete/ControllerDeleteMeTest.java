package com.didekindroid.usuario.delete;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.usuario.delete.ControllerDeleteMeIf.ReactorDeleteMeIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.api.ManagerMock.flagManageMockExecMethod;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_REPLACED_VIEW;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static org.hamcrest.CoreMatchers.is;
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

    ControllerDeleteMeIf controller;
    ManagerIf<Object> manager;

    @Before
    public void setUp()
    {
        manager = new ManagerMock<>(activityRule.getActivity());
        controller = new ControllerDeleteMe(new ViewerMock<>(manager), doReactor(), TKhandler);
    }

    @Test
    public void testUnregisterUser() throws Exception
    {
        assertThat(controller.unregisterUser(), is(false)); // False is returned in mock implementation.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackDeleteMeRemote() throws Exception
    {
        controller.processBackDeleteMeRemote(true);
        assertThat(flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL), is(MANAGER_AFTER_REPLACED_VIEW));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ReactorDeleteMeIf doReactor()
    {
        return new ReactorDeleteMeIf() {
            @Override // Used in testUnregisterUser.
            public boolean deleteMeInRemote(ControllerDeleteMeIf controller)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };
    }
}