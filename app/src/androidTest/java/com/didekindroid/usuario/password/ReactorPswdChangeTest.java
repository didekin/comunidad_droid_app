package com.didekindroid.usuario.password;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ControllerAbs;
import com.didekindroid.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import timber.log.Timber;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenReactor_1_Test.checkInitTokenCache;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.UsuarioAssertionMsg.user_password_should_be_updated;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.password.ControllerPasswordChange.ReactorPswdChange.pswdChangeReactor;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 03/02/17
 * Time: 20:31
 */
@RunWith(AndroidJUnit4.class)
public class ReactorPswdChangeTest {

    private static final String new_password = "new_password";
    private final Usuario usuario = new Usuario.UsuarioBuilder().userName(USER_PEPE.getUserName()).password(new_password).build();
    private SpringOauthToken oldToken;

    @Before
    public void doFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        oldToken = TKhandler.getTokenCache().get();
    }

    @After
    public void unDoFixture() throws UiException
    {
    }

    @AfterClass
    public static void resetScheduler()
    {
        reset();
    }

    //  =======================================================================================
    // ............................ OBSERVABLES ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testIsPasswordChanged_1() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            pswdChangeReactor.isPasswordChanged(usuario).test().assertComplete();
        } finally {
            reset();
        }
        checkCacheAndDeleteUser(true);
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * We simulate a single emitting a 0.
     */
    @Test
    public void testIsPasswordChanged_2() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            doPswdChangeReactor(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception
                {
                    return 0;
                }
            }).isPasswordChanged(usuario).test().assertFailureAndMessage(AssertionError.class, user_password_should_be_updated);
        } finally {
            reset();
        }
        checkCacheAndDeleteUser(false);
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * We simulate a single emitting a UiException.
     */
    @Test
    public void testIsPasswordChanged_3() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            doPswdChangeReactor(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception
                {
                    Timber.d("Inside call(), Thread: %s", Thread.currentThread().getName());
                    throw new UiException(new ErrorBean(USER_NAME_NOT_FOUND));
                }
            }).isPasswordChanged(usuario).test().assertError(new Predicate<Throwable>() {
                @Override
                public boolean test(Throwable throwable) throws Exception
                {
                    UiException uiException = (UiException) throwable;
                    return uiException.getErrorBean().getMessage().equalsIgnoreCase(USER_NAME_NOT_FOUND.getHttpMessage());
                }
            });
        } finally {
            reset();
        }
        checkCacheAndDeleteUser(false);
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * Case OK.
     */
    @Test
    public void testPasswordChange_1() throws UiException
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(pswdChangeReactor.passwordChange(doPswdAssertionErrorController(), usuario), is(true));
        } finally {
            reset();
        }
        checkCacheAndDeleteUser(true);
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void checkCacheAndDeleteUser(boolean isPswdUpdated) throws UiException
    {
        checkInitTokenCache();
        if (isPswdUpdated) {
            assertThat(TKhandler.getTokenCache().get(), not(is(oldToken)));
        } else {
            assertThat(TKhandler.getTokenCache().get(), is(oldToken));
        }
        usuarioDao.deleteUser();
    }

    //    .................................... MOCK REACTORS .................................

    ControllerPasswordChangeIf.ReactorPswdChangeIf doPswdChangeReactor(final Callable<Integer> callable)
    {
        return new ControllerPasswordChangeIf.ReactorPswdChangeIf() {
            @Override
            public Completable isPasswordChanged(final Usuario usuario)
            {
                Timber.d("inside isPasswordChanged(), Thread: %s", Thread.currentThread().getName());
                return Single.fromCallable(callable)
                        .flatMapCompletable(new Function<Integer, CompletableSource>() {
                            @Override
                            public CompletableSource apply(Integer passwordUpdated) throws Exception
                            {
                                Timber.d("Before assertionTrue, thread: %s", Thread.currentThread().getName());
                                assertTrue(passwordUpdated == 1, user_password_should_be_updated);
                                return oauthTokenAndInitCache(usuario);
                            }
                        });
            }

            @Override
            public boolean passwordChange(ControllerPasswordChangeIf controller, Usuario usuario)
            {
                return false;
            }
        };
    }

//    .................................... MOCK CONTROLLERS .................................

    ControllerPasswordChangeIf doPswdAssertionErrorController()
    {
        return new ControllerPasswordChangeIf() {

            ManagerIf.ControllerIf controllerAb = new ControllerAbs() {
                @Override
                public ManagerIf.ViewerIf getViewer()
                {
                    return null;
                }
            };

            @Override
            public void changePasswordInRemote(Usuario usuario)
            { }

            @Override
            public void processBackChangedPswdRemote()
            { Timber.d("!!!!!!!!!!!!!!!! SUCCESS !!!!!!!!!!!!!!!!!!"); }

            @Override
            public CompositeDisposable getSubscriptions()
            { return controllerAb.getSubscriptions(); }

            @Override
            public int clearSubscriptions()
            { return controllerAb.clearSubscriptions(); }

            @Override
            public ManagerIf.ViewerIf getViewer()
            { return controllerAb.getViewer(); }

            @Override
            public boolean isRegisteredUser()
            { return controllerAb.isRegisteredUser(); }

            @Override
            public void processReactorError(Throwable e)
            {
                Timber.d("!!!!!!!!!!!!!!!! ERROR !!!!!!!!!!!!!!!!!!");
                controllerAb.processReactorError(e);
            }
        };
    }
}