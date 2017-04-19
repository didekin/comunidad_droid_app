package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.OauthTokenReactor.OauthUpdateTokenCacheObserver;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.observers.DisposableCompletableObserver;

import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromRefreshTk;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromUserPswd;
import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdateTokenCache;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromCallable;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static io.reactivex.schedulers.Schedulers.io;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 15:15
 */
@RunWith(AndroidJUnit4.class)
public class OauthTokenReactor_1_Test {

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }


    //  ====================================================================================
    //    .................................... OBSERVABLES .................................
    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_1() throws Exception
    {
        oauthTokenFromUserPswd(USER_PEPE).test().assertValueCount(1).assertComplete().assertNoErrors();
    }

    /**
     * Synchronous execution: IO scheduler specified; we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_2() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            oauthTokenFromUserPswd(USER_PEPE).subscribeOn(io()).test().assertValueCount(1).assertComplete().assertNoErrors();
        } finally {
            reset();
        }
    }

    /**
     * Asynchronous execution: two different threads, no blocking.
     */
    @Test
    public void testOauthTokenFromRefreshTk_1() throws Exception
    {
        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenFromRefreshTk(TKhandler.getRefreshTokenValue()).test()
                .await()
                .assertValueCount(0)
                .assertNoErrors()
                .assertComplete()
                .assertTerminated();

        checkUpdateTokenCache(oldToken);
    }

    /**
     * Aynchronous execution with blocking.
     */
    @Test
    public void testOauthTokenFromRefreshTk_2() throws Exception
    {
        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenFromRefreshTk(TKhandler.getRefreshTokenValue())
                .blockingAwait();
        checkUpdateTokenCache(oldToken);
    }

    /**
     * Asynchronous execution: two different threads, no blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_1() throws Exception
    {
        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenAndInitCache(USER_PEPE).test()
                .awaitDone(4L, TimeUnit.SECONDS)
                .assertValueCount(0)
                .assertComplete()
                .assertTerminated();

        checkUpdateTokenCache(oldToken);
    }

    /**
     * Aynchronous execution with blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_2() throws Exception
    {
        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenAndInitCache(USER_PEPE)
                .blockingAwait();
        checkUpdateTokenCache(oldToken);
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onErrorCtrl method: we test that the token cache is cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_1() throws UiException
    {
        checkInitTokenCache();
        DisposableCompletableObserver disposable = error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new OauthUpdateTokenCacheObserver());
        checkNoInitCache();
        assertThat(disposable.isDisposed(), is(true));
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onComplete method: we test that the token cache is NOT cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_2() throws UiException
    {
        checkInitTokenCache();

        DisposableCompletableObserver disposable = fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception
            {
                return null;
            }
        }).subscribeWith(new OauthUpdateTokenCacheObserver());

        checkInitTokenCache();
        assertThat(disposable.isDisposed(), is(true));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * We register a new user, check the cache is null, call the disposable and check cache is initialized.
     */
    @Test
    public void testUpdateTkAndCacheFromUser() throws IOException, UiException
    {
        // Borramos el usario dado de alta por defecto.
        cleanOptions(CLEAN_PEPE);

        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_PEPE).execute().body();
        TKhandler.updateIsRegistered(true);
        checkNoInitCache();
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            tokenReactor.updateTkAndCacheFromUser(USER_PEPE);
        } finally {
            reset();
        }
        checkInitTokenCache();
    }

    @Test
    public void testOauthTokenFromRefreshTk(){

        DisposableCompletableObserver disposable = null;

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            disposable = tokenReactor.updateTkCacheFromRefreshTk(TKhandler.getRefreshTokenValue());
        } finally {
            reset();
        }
        assertThat(disposable != null && disposable.isDisposed(), is(true));
    }
}