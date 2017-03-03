package com.didekindroid.usuario.password;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.ViewerDumbImp;
import com.didekindroid.testutil.MockActivity;
import com.didekindroid.usuario.password.ControllerPasswordChangeIf.ReactorPswdChangeIf;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Completable;

import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 13:44
 */
public class ControllerPasswordChangeTest {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ControllerPasswordChangeIf controller;
    ViewerPasswordChangeIf<View, Object> viewer;
    ReactorPswdChangeIf reactor;

    @Before
    public void setUp()
    {
        viewer = new ViewerPswdInTest(activityRule.getActivity());
        reactor = new ReactorPswdInTest();
        controller = new ControllerPasswordChange(viewer, reactor);
    }

    @Test
    public void testChangePasswordInRemote() throws Exception
    {
        controller.changePasswordInRemote(USER_PEPE);
        assertThat(flagForExecution.getAndSet(0), is(123));
    }

    @Test
    public void testProcessBackChangedPswdRemote() throws Exception
    {
        controller.processBackChangedPswdRemote();
        assertThat(flagForExecution.getAndSet(0), is(31));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    static class ViewerPswdInTest extends ViewerDumbImp<View, Object> implements
            ViewerPasswordChangeIf<View, Object> {

        protected ViewerPswdInTest(Activity activity)
        {
            super(activity);
        }

        @Override
        public View doViewInViewer(Activity activity)
        {
            return new View(activity);
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

        @Override
        public void replaceView(Object initParams)
        {
            assertThat(flagForExecution.getAndSet(31), is(0));
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
            assertThat(flagForExecution.getAndSet(123), is(0));
            return false;
        }
    }

}