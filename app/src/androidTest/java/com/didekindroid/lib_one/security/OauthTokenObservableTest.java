package com.didekindroid.lib_one.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenAndInitCache;
import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenFromRefreshTk;
import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenFromUserPswd;
import static com.didekindroid.lib_one.security.OauthTokenObservable.oauthTokenInitCacheUpdateRegister;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkInitTokenCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.checkUpdateTokenCache;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
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

    TokenIdentityCacher tkCacher = (TokenIdentityCacher) secInitializer.get().getTkCacher();

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOneUser(user_crodrigo);
    }

    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_1() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        oauthTokenFromUserPswd(user_crodrigo).test().assertValueCount(1).assertComplete().assertNoErrors();
    }

    /**
     * Synchronous execution: IO scheduler specified; we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     */
    @Test
    public void testOauthTokenFromUserPswd_2() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());
        try {
            trampolineReplaceIoScheduler();
            oauthTokenFromUserPswd(user_crodrigo).subscribeOn(io()).test().assertValueCount(1).assertComplete().assertNoErrors();
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
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());

        SpringOauthToken oldToken = tkCacher.getTokenCache().get();
        oauthTokenFromRefreshTk(tkCacher.getRefreshTokenValue()).test()
                .await()
                .assertValueCount(0)
                .assertNoErrors()
                .assertComplete()
                .assertTerminated();

        checkUpdateTokenCache(oldToken, tkCacher);
    }

    /**
     * Aynchronous execution with blocking.
     */
    @Test
    public void testOauthTokenFromRefreshTk_2() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());

        SpringOauthToken oldToken = tkCacher.getTokenCache().get();
        oauthTokenFromRefreshTk(tkCacher.getRefreshTokenValue())
                .blockingAwait();
        checkUpdateTokenCache(oldToken, tkCacher);
    }

    /**
     * No blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_1() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());

        SpringOauthToken oldToken = tkCacher.getTokenCache().get();
        oauthTokenAndInitCache(user_crodrigo).test()
                .awaitDone(4L, SECONDS)
                .assertValueCount(0)
                .assertComplete()
                .assertTerminated();

        checkUpdateTokenCache(oldToken, tkCacher);
    }

    /**
     * With blocking.
     */
    @Test
    public void testOauthTokenAndInitCache_2() throws Exception
    {
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body(), is(true));
        updateSecurityData(user_crodrigo.getUserName(), user_crodrigo.getPassword());

        SpringOauthToken oldToken = tkCacher.getTokenCache().get();
        // For completeness, to test the change in the registered status, we 'unregister' the user.
        tkCacher.updateIsRegistered(false);
        oauthTokenAndInitCache(user_crodrigo)
                .blockingAwait();
        checkUpdateTokenCache(oldToken, tkCacher);
        // Check register status.
        assertThat(tkCacher.isRegisteredUser(), is(true));
    }

    @Test
    public void test_OauthTokenInitCacheUpdateRegister() throws Exception
    {
        usuarioMockDao.regComuAndUserAndUserComu(comu_real_rodrigo).execute().body();
        // User not registered.
        assertThat(tkCacher.isRegisteredUser(), is(false));
        assertThat(tkCacher.getTokenCache().get(), nullValue());

        oauthTokenInitCacheUpdateRegister(user_crodrigo).blockingAwait();
        checkInitTokenCache(tkCacher);
        assertThat(tkCacher.isRegisteredUser(), is(true));
    }
}
