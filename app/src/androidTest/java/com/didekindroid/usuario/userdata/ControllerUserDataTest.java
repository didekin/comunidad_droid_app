package com.didekindroid.usuario.userdata;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.ControllerAbsTest;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.testutil.MockActivity;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 14:29
 */
public class ControllerUserDataTest extends ControllerAbsTest {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ControllerUserDataIf controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new ControllerUserData(new ViewerForUserDataTest(activityRule.getActivity()), doReactor());
    }

    @Test
    public void testLoadUserData() throws Exception
    {
        controller.loadUserData();
        assertThat(flagForExecution.getAndSet(0), is(3));
    }

    @Test
    public void testModifyUser() throws Exception
    {
        controller.modifyUser(USER_DROID, new Usuario.UsuarioBuilder().copyUsuario(USER_DROID)
                .userName("new_user_droid")
                .build());
        assertThat(flagForExecution.getAndSet(0), is(7));
    }

    @Test
    public void testProcessBackUserDataLoaded() throws Exception
    {
        controller.processBackUserDataLoaded(USER_DROID);
        assertThat(flagForExecution.getAndSet(0), is(11));
    }

    @Test
    public void testProcessBackUserModified() throws Exception
    {
        controller.processBackUserModified();
        assertThat(flagForExecution.getAndSet(0), is(23));
    }

    @Override
    @Test
    public void testProcessReactorError()
    {
        controller.processReactorError(new UiException(new ErrorBean(TOKEN_NULL)));
        assertThat(flagForExecution.getAndSet(0), is(37));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ControllerUserDataIf.ReactorUserDataIf doReactor()
    {

        return new ControllerUserDataIf.ReactorUserDataIf() {
            @Override
            public boolean loadUserData(ControllerUserDataIf controller)
            {
                assertThat(flagForExecution.getAndSet(3), is(0));
                return flagForExecution.get() == 3;
            }

            @Override
            public boolean modifyUser(ControllerUserDataIf controller, Usuario oldUser, Usuario newUser)
            {
                assertThat(flagForExecution.getAndSet(7), is(0));
                return flagForExecution.get() == 7;
            }
        };
    }

    static class ViewerForUserDataTest extends ViewerDumbImp<View, Object> implements
            ViewerUserDataIf<View, Object> {

        public ViewerForUserDataTest(Activity activity)
        {
            super(activity);
            viewInViewer = new View(activity);
        }

        @Override
        public View doViewInViewer(Activity activity)
        {
            return new View(activity);
        }

        @Override
        public void initUserDataInView()
        {
        }

        @Override
        public String[] getDataChangedFromView()
        {
            return new String[0];
        }

        @Override
        public boolean checkUserData()
        {
            return false;
        }

        @Override
        public UserChangeToMake whatDataChangeToMake()
        {
            return null;
        }

        @Override
        public boolean modifyUserData(UserChangeToMake userChangeToMake)
        {
            return false;
        }

        @Override
        public void processBackUsuarioInView(Usuario usuario)
        {
            assertThat(flagForExecution.getAndSet(11), is(0));
        }

        @Override
        public void replaceView(Object initParams)
        {
            assertThat(flagForExecution.getAndSet(23), is(0));
        }


        @Override  // Used in testProcessReactorError()
        public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
        {
            Timber.d("====================== processControllerError() ====================");
            assertThat(flagForExecution.getAndSet(37), is(0));
            assertThat(ui.getErrorBean().getMessage(), is(TOKEN_NULL.getHttpMessage()));
            return null;
        }
    }

}