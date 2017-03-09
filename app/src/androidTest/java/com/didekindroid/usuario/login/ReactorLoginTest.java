package com.didekindroid.usuario.login;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.ViewerMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
import com.didekinlib.model.usuario.Usuario;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.functions.Predicate;

import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.cleanTkCacheActionBoolean;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.ReactorLogin.loginReactor;
import static com.didekindroid.usuario.login.ReactorLogin.loginSingle;
import static com.didekindroid.usuario.login.ReactorLogin.loginUpdateTkCache;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static io.reactivex.schedulers.Schedulers.io;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 25/01/17
 * Time: 18:41
 */
public class ReactorLoginTest {

    public static final String IS_SENT_PSWSD_AFTER = "sent password";
    public static final String IS_NOT_SENT_PSWSD_AFTER = "not sent password";
    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    //  ====================================================================================
    //    .......................... OBSERVABLES .................................
    //  ====================================================================================

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
            reset();
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
            reset();
        }
        checkNoInitCache();
        cleanOptions(CLEAN_DROID);
    }

    /**
     * We use a mock ReactorLogin to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void testPasswordSendSingle_1() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        new ReactorLoginForTest(true).loginPswdSendSingle(USER_DROID.getUserName()).test().assertResult(true);
        // Check cache cleaning.
        checkNoInitCache();

        // Es necesario conseguir un nuevo token.
        TKhandler.initIdentityCache(Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
        usuarioDao.deleteUser();
        cleanWithTkhandler();
    }

    /**
     * We use a mock ReactorLogin to avoid changing user password in database: it would make impossible to delete user afterwards.
     */
    @Test
    public void testPasswordSendSingle_2() throws UiException, IOException
    {
        signUpAndUpdateTk(COMU_REAL_DROID);

        new ReactorLoginForTest(false).loginPswdSendSingle(USER_DROID.getUserName()).test().assertResult(false);
        // Check cache is not cleaned.
        checkInitTokenCache();

        // NO es necesario conseguir un nuevo token.
        usuarioDao.deleteUser();
        cleanWithTkhandler();
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
        TKhandler.updateIsRegistered(true);

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            loginReactor.validateLogin(new ControllerLoginForTest(), USER_DROID);
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
        } finally {
            reset();
        }
        cleanOptions(CLEAN_DROID);
    }

    @Test
    public void testSendPasswordToUser_1()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            new ReactorLoginForTest(true).sendPasswordToUser(new ControllerLoginForTest(), USER_JUAN);
        } finally {
            reset();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(IS_SENT_PSWSD_AFTER));
    }

    @Test
    public void testSendPasswordToUser_2()
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            new ReactorLoginForTest(false).sendPasswordToUser(new ControllerLoginForTest(), USER_JUAN);
        } finally {
            reset();
        }
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(IS_NOT_SENT_PSWSD_AFTER));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    /*  ============================================================================================*/

    class ReactorLoginForTest extends ReactorLogin {

        final boolean isSentPassword;

        public ReactorLoginForTest(Boolean isSentPswd)
        {
            this.isSentPassword = isSentPswd;
        }

        /**
         * Mock variant without changing password and deleting access token remotely.
         */
        @Override
        public Single<Boolean> loginPswdSendSingle(final String email)
        {
            return fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception
                {
                    return isSentPassword;
                }
            }).doOnSuccess(cleanTkCacheActionBoolean);
        }

        /**
         * Mock variant to call mock loginPswdSendSingle.
         */
        @Override
        public boolean sendPasswordToUser(ControllerLoginIf controller, Usuario usuario)
        {
            return controller.getSubscriptions().add(
                    loginPswdSendSingle(usuario.getUserName())
                            .subscribeOn(io())
                            .observeOn(mainThread())
                            .subscribeWith(new LoginPswdSendObserver(controller))
            );
        }
    }

    class ControllerLoginForTest extends ControllerIdentityAbs implements ControllerLoginIf {

        @Override
        public void validateLoginRemote(Usuario usuario)
        {
        }

        @Override  // Used in testValidateLogin_1
        public void processBackLoginRemote(Boolean isLoginOk)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void doDialogPositiveClick(Usuario usuario)
        {
        }

        @Override
        public ManagerIncidSeeIf.ViewerIf<View,Object> getViewer()
        {
            return new ViewerMock<>(new ManagerMock<>(activityRule.getActivity()));
        }

        @Override  // Used in testSendPasswordToUser_*
        public void processBackDialogPositiveClick(Boolean isSendPassword)
        {
            if (isSendPassword) {
                assertThat(flagMethodExec.getAndSet(IS_SENT_PSWSD_AFTER), is(BEFORE_METHOD_EXEC));
            } else {
                assertThat(flagMethodExec.getAndSet(IS_NOT_SENT_PSWSD_AFTER), is(BEFORE_METHOD_EXEC));
            }
        }
    }
}