package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.http.ErrorBean;
import com.didekin.http.oauth2.SpringOauthToken;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.OauthTokenReactor.OauthUpdateTokenCacheObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;

import static com.didekin.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromRefreshTk;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromUserPswd;
import static com.didekindroid.security.OauthTokenReactor.tokenReactor;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static io.reactivex.schedulers.Schedulers.io;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
        SpringOauthToken oldToken = TKhandler.getAccessTokenInCache();
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
        SpringOauthToken oldToken = TKhandler.getAccessTokenInCache();
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
        SpringOauthToken oldToken = TKhandler.getAccessTokenInCache();
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
        SpringOauthToken oldToken = TKhandler.getAccessTokenInCache();
        oauthTokenAndInitCache(USER_PEPE)
                .blockingAwait();
        checkUpdateTokenCache(oldToken);
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onError method: we test that the token cache is cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_1() throws UiException
    {
        checkInitTokenCache();
        Completable.error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new OauthUpdateTokenCacheObserver());
        checkNoInitCache();

    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onComplete method: we test that the token cache is NOT cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_2() throws UiException
    {
        checkInitTokenCache();

        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception
            {
                return null;
            }
        }).subscribeWith(new OauthUpdateTokenCacheObserver());

        checkInitTokenCache();
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
        // We delete from everywhere the default user for the rest of the tests.
        cleanOptions(CLEAN_PEPE);

        AppUserComuServ.regComuAndUserAndUserComu(COMU_REAL_PEPE).execute().body();
        checkNoInitCache();
        try {
            trampolineReplaceIoScheduler();
            tokenReactor.updateTkAndCacheFromUser(USER_PEPE);
        } finally {
            reset();
        }
        checkInitTokenCache();
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    private void checkUpdateTokenCache(SpringOauthToken oldToken) throws UiException
    {
        assertThat(TKhandler.getAccessTokenInCache(), not(is(oldToken)));
        checkInitTokenCache();
    }

    private void checkInitTokenCache() throws UiException
    {
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.getAccessTokenInCache().getValue().isEmpty(), is(false));
        assertThat(TKhandler.getRefreshTokenValue().isEmpty(), is(false));
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
    }

    private void checkNoInitCache() throws UiException
    {
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
    }
}
