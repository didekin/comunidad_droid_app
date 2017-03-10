package com.didekindroid.usuario.login;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
public class ControllerLoginTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ControllerLoginIf controller;
    ManagerIf<Object> manager;

    @Before
    public void setUp() throws Exception
    {
        manager = new ManagerMock<>(activityRule.getActivity());
        controller = new ControllerLogin(new ViewerLoginForTest(manager), doReactor(), TKhandler);
    }

    @Test
    public void testValidateLoginRemote() throws Exception
    {
        controller.validateLoginRemote(UsuarioDataTestUtils.USER_PEPE);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackLoginRemote() throws Exception
    {
        controller.processBackLoginRemote(false);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testDoDialogPositiveClick() throws Exception
    {
        controller.doDialogPositiveClick(UsuarioDataTestUtils.USER_JUAN);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackDialogPositiveClick() throws Exception
    {
        controller.processBackDialogPositiveClick(false);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ControllerLoginIf.ReactorLoginIf doReactor()
    {

        return new ControllerLoginIf.ReactorLoginIf() {

            @Override
            public Single<Boolean> loginPswdSendSingle(String email)
            {
                return null;
            }

            @Override
            public boolean validateLogin(ControllerLoginIf controller, Usuario usuario)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
            }

            @Override
            public boolean sendPasswordToUser(ControllerLoginIf controller, Usuario usuario)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
            }
        };
    }

    static class ViewerLoginForTest extends ViewerMock<View, Object> implements
            ViewerLoginIf<View, Object> {

        protected ViewerLoginForTest(ManagerIf<Object> manager)
        {
            super(manager);
            viewInViewer = new View(manager.getActivity());
        }

        @Override
        public boolean checkLoginData()
        {
            return false;
        }

        @Override
        public void showDialog(String userName)
        {
        }

        @Override
        public String[] getLoginDataFromView()
        {
            return new String[0];
        }

        @Override
        public void processLoginBackInView(boolean isLoginOk)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void processBackSendPswdInView(boolean isSendPassword)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void doDialogNegativeClick()
        {
        }
    }
}