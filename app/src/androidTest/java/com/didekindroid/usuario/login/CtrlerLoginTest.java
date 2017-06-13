package com.didekindroid.usuario.login;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.CtrlerLogin.loginPswdSendSingle;
import static com.didekindroid.usuario.login.CtrlerLogin.loginSingle;
import static com.didekindroid.usuario.login.CtrlerLogin.loginUpdateTkCache;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 14:17
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerLoginTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<LoginAc> activityRule = new ActivityTestRule<>(LoginAc.class, true, true);

    CtrlerLoginIf controller;
    LoginAc activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        controller = new CtrlerLogin();
    }

    @After
    public void cleanUp() throws UiException
    {
        controller.clearSubscriptions();
        resetAllSchedulers();
    }

    //    .................................... OBSERVABLES .................................
    @Test
    public void testLoginSingle_1() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(USER_DROID).test().assertResult(true);

        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginSingle_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(new Usuario.UsuarioBuilder().userName("user@notfound.com").password(USER_DROID.getPassword()).build())
                .test().assertError(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable throwable) throws Exception
            {
                UiException uiException = (UiException) throwable;
                return uiException.getErrorBean().getMessage().equalsIgnoreCase(USER_NAME_NOT_FOUND.getHttpMessage());
            }
        });

        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginSingle_3() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                .test().assertResult(false);

        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginUpdateTkCache_1() throws UiException, IOException
    {
        TKhandler.updateIsRegistered(userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body());

        try {
            trampolineReplaceIoScheduler();
            loginUpdateTkCache(USER_DROID).test().assertResult(true);
        } finally {
            resetAllSchedulers();
        }
        checkInitTokenCache();
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testLoginUpdateTkCache_2() throws UiException, IOException
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        try {
            trampolineReplaceIoScheduler();
            loginUpdateTkCache(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                    .test().assertResult(false);
        } finally {
            resetAllSchedulers();
        }
        checkNoInitCache();
        cleanOptions(CLEAN_DROID);
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void testLoginPswdSendSingle() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        Callable<Boolean> mockCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return true;
            }
        };

        loginPswdSendSingle(mockCallable).test().assertResult(true);
        // Check cache cleaning.
        checkNoInitCache();

        // Es necesario conseguir un nuevo token.
        TKhandler.initIdentityCache(Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testValidateLogin() throws Exception
    {
        assertThat(controller.validateLogin(new TestDisposableSingleObserver(), USER_PEPE), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testDoDialogPositiveClick() throws Exception
    {
        assertThat(controller.doDialogPositiveClick(new TestDisposableSingleObserver(), USER_JUAN), is(true));
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    static class TestDisposableSingleObserver extends DisposableSingleObserver<Boolean> {
        @Override
        public void onSuccess(Boolean aBoolean)
        {
            dispose();
        }

        @Override
        public void onError(Throwable e)
        {
            dispose();
            fail();
        }
    }
}