package com.didekindroid.usuario.login;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdatedCacheAfterPswd;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.CtrlerUsuario.loginPswdSendSingle;
import static com.didekindroid.usuario.login.CtrlerUsuario.loginSingle;
import static com.didekindroid.usuario.login.CtrlerUsuario.loginUpdateTkCache;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
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
public class CtrlerUsuario_Login_Test {

    CtrlerUsuario controller;

    @Before
    public void setUp() throws Exception
    {
        controller = new CtrlerUsuario();
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
    public void test_LoginUpdateTkCache_1() throws UiException, IOException
    {
        TKhandler.updateIsRegistered(userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body());
        checkNoInitCache(); // Precondition.
        loginUpdateTkCache(USER_DROID).test().assertResult(true);
        checkInitTokenCache();
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void test_LoginUpdateTkCache_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginUpdateTkCache(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                .test().assertResult(false);
        checkUpdatedCacheAfterPswd(false, TKhandler.getTokenCache().get());
        cleanOptions(CLEAN_DROID);
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_1() throws UiException, IOException, InterruptedException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginPswdSendSingle(new SendPswdCallable()).test().assertResult(true);
        // Check cache cleaning.
        checkNoInitCache();
        finishLoginPswdSendSingle();
    }

    /**
     * We use a mock callable to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void test_LoginPswdSendSingle_2() throws UiException, IOException, InterruptedException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);
        checkInitTokenCache(); // Precondition.
        loginPswdSendSingle(new SendPswdCallableError()).test().assertFailure(UiException.class);
        // Check cache hasn't changed.
        checkInitTokenCache();
        finishLoginPswdSendSingle();
    }

    //    .................................... INSTANCE METHODS .................................

    @Test
    public void testValidateLogin() throws Exception
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.validateLogin(new TestDisposableSingleObserver(), USER_DROID), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        cleanOptions(CLEAN_DROID);
    }

    @Test   // With mock callable to avoid change identity data in cache.
    public void test_SendNewPassword() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.sendNewPassword(new SendPswdCallable(), new TestDisposableSingleObserver()), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void finishLoginPswdSendSingle() throws UiException
    {
        // Es necesario conseguir un nuevo token.
        TKhandler.initIdentityCache(Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

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
            Timber.d("============= %s =============", e.getClass().getName());
            fail();
        }
    }

    static class SendPswdCallable implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception
        {
            return true;
        }
    }

    static class SendPswdCallableError implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception
        {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}