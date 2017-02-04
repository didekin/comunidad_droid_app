package com.didekindroid.usuario.login;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import timber.log.Timber;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.OauthTokenReactor_1_Test.checkInitTokenCache;
import static com.didekindroid.security.OauthTokenReactor_1_Test.checkNoInitCache;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.LoginReactor.loginPswdSendSingle;
import static com.didekindroid.usuario.login.LoginReactor.loginReactor;
import static com.didekindroid.usuario.login.LoginReactor.loginSingle;
import static com.didekindroid.usuario.login.LoginReactor.loginUpdateTkCache;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/01/17
 * Time: 18:41
 */
public class LoginReactorTest {

    //  ====================================================================================
    //    .......................... OBSERVABLES .................................
    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testLoginSingle_1() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(USER_DROID).test().assertResult(true);

        cleanOptions(CLEAN_DROID);
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
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

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testLoginSingle_3() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginSingle(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                .test().assertResult(false);

        cleanOptions(CLEAN_DROID);
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testPasswordSendSingle_1() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        loginPswdSendSingle(USER_DROID.getUserName()).test().assertResult(true);
        // Es necesario conseguir un nuevo token. La validación del antiguo falla por el cambio de password.
        TKhandler.initIdentityCache(Oauth2.getRefreshUserToken(TKhandler.getRefreshTokenValue()));
        assertThat(usuarioDao.getUserData().getPassword(), not(USER_DROID.getPassword()));

        usuarioDao.deleteUser();
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testLoginUpdateTkCache_1() throws UiException, IOException
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        try {
            trampolineReplaceIoScheduler();
            loginUpdateTkCache(USER_DROID).test().assertResult(true);
        } finally {
            reset();
        }
        checkInitTokenCache();
        cleanOptions(CLEAN_DROID);
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testLoginUpdateTkCache_2() throws UiException, IOException
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        try {
            trampolineReplaceIoScheduler();
            loginUpdateTkCache(new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build())
                    .test().assertResult(false);
        } finally {
            reset();
        }
        checkNoInitCache();
        cleanOptions(CLEAN_DROID);
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testValidateLogin_1() throws UiException, IOException
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            loginReactor.validateLogin(doLoginController(), USER_DROID);
        } finally {
            reset();
        }
        cleanOptions(CLEAN_DROID);
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testValidateLogin_2() throws UiException, IOException
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body();

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            loginReactor.validateLogin(doLoginController(), new Usuario.UsuarioBuilder().userName(USER_DROID.getUserName()).password("password_wrong").build());
        } finally {
            reset();
        }
        cleanOptions(CLEAN_DROID);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    LoginControllerIf doLoginController()
    {
        return new LoginControllerIf() {
            @Override
            public boolean checkLoginData()
            {
                return false;
            }

            @Override
            public void validateLoginRemote()
            {

            }

            @Override
            public void processBackLoginRemote(Boolean isLoginOk)
            {
                if (isLoginOk){
                    try {
                        Timber.d("¡¡¡¡¡¡¡¡¡¡¡¡ Login OK  ¡¡¡¡¡¡¡¡¡¡¡¡¡");
                        checkInitTokenCache();
                    } catch (UiException e) {
                        fail();
                    }
                } else {
                    try {
                        Timber.d("¡¡¡¡¡¡¡¡¡¡¡ Login NOT OK ¡¡¡¡¡¡¡¡¡¡¡¡");
                        checkNoInitCache();
                    } catch (UiException e) {
                        fail();
                    }
                }
            }

            @Override
            public void processBackSendPassword(Boolean isSendPassword)
            {

            }

            @Override
            public void doDialogPositiveClick()
            {

            }

            @Override
            public void doDialogNegativeClick()
            {

            }

            @Override
            public CompositeDisposable getSubscriptions()
            {
                return new CompositeDisposable();
            }

            @Override
            public void processBackErrorInReactor(Throwable e)
            {

            }
        };
    }
}