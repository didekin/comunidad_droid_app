package com.didekindroid.usuario.password;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.ManagerIf;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.ViewerMock;
import com.didekindroid.usuario.password.ControllerPasswordChangeIf.ReactorPswdChangeIf;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;

import static com.didekindroid.ManagerMock.flagManageMockExecMethod;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_REPLACED_VIEW;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 13:44
 */
public class ControllerPasswordChangeTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ControllerPasswordChangeIf controller;
    ViewerPasswordChangeIf<View, Object> viewer;
    ReactorPswdChangeIf reactor;
    ManagerIf<Object> manager;

    @Before
    public void setUp()
    {
        manager = new ManagerMock<>(activityRule.getActivity());
        viewer = new ViewerPswdInTest(manager);
        reactor = new ReactorPswdInTest();
        controller = new ControllerPasswordChange(viewer, reactor, TKhandler);
    }

    @Test
    public void testChangePasswordInRemote() throws Exception
    {
        controller.changePasswordInRemote(USER_PEPE);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackChangedPswdRemote() throws Exception
    {
        controller.processBackChangedPswdRemote();
        assertThat(flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL), is(MANAGER_AFTER_REPLACED_VIEW));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    static class ViewerPswdInTest extends ViewerMock<View, Object> implements
            ViewerPasswordChangeIf<View, Object> {

        protected ViewerPswdInTest(ManagerIf<Object> manager)
        {
            super(manager);
            viewInViewer = new View(manager.getActivity());
        }

        @Override
        public String[] getPswdDataFromView()
        {
            return new String[0];
        }

        @Override
        public boolean checkLoginData()
        {
            return false;
        }
    }

    static class ReactorPswdInTest implements ReactorPswdChangeIf {

        @Override
        public Completable isPasswordChanged(Usuario usuario)
        {
            return null;
        }

        @Override
        public boolean passwordChange(ControllerPasswordChangeIf controller, Usuario password)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
        }
    }
}