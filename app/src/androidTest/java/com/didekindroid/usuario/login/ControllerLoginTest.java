package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.ViewerDumbImp;
import com.didekindroid.testutil.MockActivity;
import com.didekindroid.usuario.testutil.UsuarioDataTestUtils;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Single;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
public class ControllerLoginTest {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ControllerLoginIf controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new ControllerLogin(new ViewerLoginForTest(activityRule.getActivity()), doReactor());
    }

    @Test
    public void testValidateLoginRemote() throws Exception
    {
        controller.validateLoginRemote(UsuarioDataTestUtils.USER_PEPE);
        assertThat(flagForExecution.getAndSet(0), is(13));
    }

    @Test
    public void testProcessBackLoginRemote() throws Exception
    {
        controller.processBackLoginRemote(false);
        assertThat(flagForExecution.getAndSet(0), is(77));
    }

    @Test
    public void testDoDialogPositiveClick() throws Exception
    {
        controller.doDialogPositiveClick(UsuarioDataTestUtils.USER_JUAN);
        assertThat(flagForExecution.getAndSet(0), is(17));
    }

    @Test
    public void testProcessBackDialogPositiveClick() throws Exception
    {
        controller.processBackDialogPositiveClick(false);
        assertThat(flagForExecution.getAndSet(0), is(22));
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
                assertThat(flagForExecution.getAndSet(13), is(0));
                return flagForExecution.get() == 13;
            }

            @Override
            public boolean sendPasswordToUser(ControllerLoginIf controller, Usuario usuario)
            {
                assertThat(flagForExecution.getAndSet(17), is(0));
                return flagForExecution.get() == 17;
            }
        };
    }

    static class ViewerLoginForTest extends ViewerDumbImp<View, Object> implements
            ViewerLoginIf<View, Object> {

        protected ViewerLoginForTest(Activity activity)
        {
            super(activity);
        }

        @Override
        public View doViewInViewer(Activity activity)
        {
            return new View(activity);
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
            assertThat(flagForExecution.getAndSet(77), is(0));
        }

        @Override
        public void processBackSendPswdInView(boolean isSendPassword)
        {
            assertThat(flagForExecution.getAndSet(22), is(0));
        }

        @Override
        public void doDialogNegativeClick()
        {
        }

        @Override
        public void replaceView(Object initParams)
        {
        }
    }
}