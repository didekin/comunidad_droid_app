package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromRefreshTk;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.security.OauthTokenObservable.oauthTokenInitCacheUpdateRegister;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkUpdateTokenCache;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static io.reactivex.schedulers.Schedulers.io;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 15:15
 */
@RunWith(AndroidJUnit4.class)
public class OauthTokenObservableTest {

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_PEPE);
    }

    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_1() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        oauthTokenFromUserPswd(USER_PEPE).test().assertValueCount(1).assertComplete().assertNoErrors();
    }

    /**
     * Synchronous execution: IO scheduler specified; we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_2() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
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
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

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
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenFromRefreshTk(TKhandler.getRefreshTokenValue())
                .blockingAwait();
        checkUpdateTokenCache(oldToken);
    }

    /**
     * No blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_1() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        oauthTokenAndInitCache(USER_PEPE).test()
                .awaitDone(4L, SECONDS)
                .assertValueCount(0)
                .assertComplete()
                .assertTerminated();

        checkUpdateTokenCache(oldToken);
    }

    /**
     * With blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_2() throws Exception
    {
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        SpringOauthToken oldToken = TKhandler.getTokenCache().get();
        // For completeness, to test the change in the registered status, we 'unregister' the user.
        TKhandler.updateIsRegistered(false);
        oauthTokenAndInitCache(USER_PEPE)
                .blockingAwait();
        checkUpdateTokenCache(oldToken);
        // Check register status.
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @Test
    public void test_OauthTokenInitCacheUpdateRegister() throws Exception
    {
        userComuDaoRemote.regComuAndUserAndUserComu(COMU_ESCORIAL_PEPE).execute().body();
        // User not registered.
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getTokenCache().get(), nullValue());

        oauthTokenInitCacheUpdateRegister(USER_PEPE).blockingAwait();
        checkInitTokenCache();
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }
}
