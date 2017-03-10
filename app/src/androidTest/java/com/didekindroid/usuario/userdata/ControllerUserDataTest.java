package com.didekindroid.usuario.userdata;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_REPLACED_VIEW;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 14:29
 */
public class ControllerUserDataTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ManagerIf<Object> manager;
    ControllerUserData controller;

    @Before
    public void setUp() throws Exception
    {
        manager = new ManagerMock<>(activityRule.getActivity());
        controller = new ControllerUserData(new ViewerForUserDataTest(manager), doReactor(), TKhandler);
    }

    @Test
    public void testLoadUserData() throws Exception
    {
        controller.loadUserData();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testModifyUser() throws Exception
    {
        controller.modifyUser(USER_DROID, new Usuario.UsuarioBuilder().copyUsuario(USER_DROID)
                .userName("new_user_droid")
                .build());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackUserDataLoaded() throws Exception
    {
        controller.processBackUserDataLoaded(USER_DROID);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    @Test
    public void testProcessBackUserModified() throws Exception
    {
        controller.processBackUserModified();
        assertThat(ManagerMock.flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL),
                is(MANAGER_AFTER_REPLACED_VIEW));
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
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
            }

            @Override
            public boolean modifyUser(ControllerUserDataIf controller, Usuario oldUser, Usuario newUser)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return flagMethodExec.get().equals(AFTER_METHOD_EXEC);
            }
        };
    }

    static class ViewerForUserDataTest extends ViewerMock<View, Object> implements
            ViewerUserDataIf<View, Object> {

        public ViewerForUserDataTest(ManagerIf<Object> manager)
        {
            super(manager);
            new View(manager.getActivity());
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
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
        }
    }

}