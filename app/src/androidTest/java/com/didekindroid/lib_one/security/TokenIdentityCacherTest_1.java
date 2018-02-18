package com.didekindroid.lib_one.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.SecurityTestUtils.doSpringOauthToken;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.util.IoHelper.readStringFromFile;
import static com.didekinlib.http.auth.AuthClient.doBearerAccessTkHeader;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacherTest_1 {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Activity activity;
    TokenIdentityCacher tkCacher;

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
        activity = activityRule.getActivity();
        tkCacher = (TokenIdentityCacher) secInitializer.get().getTkCacher();
    }

    // ===================================== TESTS ==========================================

    @Test
    public void testInitTokenAndBackFile() throws Exception
    {
        // Precondition: no file with refreshToken. We receive a fully initialized token instance.
        assertThat(tkCacher.getRefreshTokenFile(), notNullValue());
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(false));
        assertThat(tkCacher.getRefreshTokenValue(), nullValue());
        assertThat(tkCacher.getTokenCache().get(), nullValue());

        SpringOauthToken springOauthToken = doSpringOauthToken();
        tkCacher.initIdentityCache(springOauthToken);
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(true));
        assertThat(tkCacher.getRefreshTokenValue(), is(springOauthToken.getRefreshToken().getValue()));
        assertThat(tkCacher.getTokenCache().get(), is(springOauthToken));
    }

    @Test
    public void testCleanCacheAndBckFile() throws UiException
    {
        // Preconditions: there exist token data and file.
        SpringOauthToken springOauthToken = doSpringOauthToken();
        tkCacher.initIdentityCache(springOauthToken);

        tkCacher.cleanIdentityCache();
        // Assertions.
        assertThat(tkCacher.getRefreshTokenValue(), nullValue());
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(false));
        assertThat(tkCacher.getTokenCache().get(), nullValue());
    }

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition: no file with refreshToken.
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(false));

        SpringOauthToken springOauthToken = doSpringOauthToken();
        String bearerTk = doBearerAccessTkHeader(springOauthToken);
        assertThat(bearerTk, equalTo("Bearer " + springOauthToken.getValue()));
    }

    @Test
    public void testUpdateIsRegistered_1() throws Exception
    {
        tkCacher.updateIsRegistered(false);
        assertThat(tkCacher.isRegisteredUser(), is(false));
        tkCacher.updateIsRegistered(true);
        assertThat(tkCacher.isRegisteredUser(), is(true));
    }

    @Test
    public void testUpdateIsRegistered_2() throws Exception
    {
        // Precondition: registered flag and  GCM token sent to server flags BOTH true.
        tkCacher.updateIsRegistered(true);
        tkCacher.updateIsGcmTokenSentServer(true);
        // Update to false BOTH: registered flag and GCM token sent to server flag.
        tkCacher.updateIsRegistered(false);
        assertThat(tkCacher.isGcmTokenSentServer(), is(false));
        // Update to true ONE: registerd flag.
        tkCacher.updateIsRegistered(true);
        assertThat(tkCacher.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void test_IsGcmTokenSentServer() throws Exception
    {
        // Precondition: the user is registered.
        tkCacher.updateIsRegistered(true);
        // Exec.
        tkCacher.updateIsGcmTokenSentServer(true);
        // Check.
        assertThat(tkCacher.isGcmTokenSentServer(), is(true));
        // Exec.
        tkCacher.updateIsGcmTokenSentServer(false);
        // Check.
        assertThat(tkCacher.isGcmTokenSentServer(), is(false));
    }

    // ===================================== ACTIONS TESTS =========================================

    @Test
    public void testInitTokenAction() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        tkCacher.initIdentityCache(token);
        checkSiCacheAndFile(token);
    }

    @Test
    public void testCleanTokenCacheAction() throws Exception
    {
        // No user registered. We test for the non-nullity of refreshTokenFile.
        tkCacher.cleanIdentityCache();
        checkNoCacheAndFile();
    }

// ===================================== FUNCTIONS TESTS =========================================

    @Test
    public void testInitTokenAndRegisterFunc_1() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        assertThat(tkCacher.getInitTokenAndRegisterFunc().apply(true, token), is(true));
        checkSiCacheAndFile(token);
        assertThat(tkCacher.isRegisteredUser(), is(true));
    }

    @Test
    public void testInitTokenAndRegisterFunc_2() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        tkCacher.getInitTokenAndRegisterFunc().apply(false, token);
        checkNoCacheAndFile();
        assertThat(tkCacher.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_1() throws Exception
    {
        initTokenHelper();
        tkCacher.getCleanIdentityFunc().apply(true);
        checkNoCacheAndFile();
        assertThat(tkCacher.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_2() throws Exception
    {
        SpringOauthToken token = initTokenHelper();
        tkCacher.getCleanIdentityFunc().apply(false);
        checkSiCacheAndFile(token);
        assertThat(tkCacher.isRegisteredUser(), is(true));
    }

    // ............................... HELPERS ..............................

    private SpringOauthToken initTokenHelper() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        tkCacher.getInitTokenAndRegisterFunc().apply(true, token);
        assertThat(tkCacher.getTokenCache().get(), is(token));
        assertThat(tkCacher.isRegisteredUser(), is(true));
        return token;
    }

    private void checkNoCacheAndFile()
    {
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(false));
        assertThat(tkCacher.getTokenCache().get(), is(nullValue()));
    }

    private void checkSiCacheAndFile(SpringOauthToken token)
    {
        assertThat(tkCacher.getRefreshTokenFile().exists(), is(true));
        assertThat(readStringFromFile(tkCacher.getRefreshTokenFile()), is(token.getRefreshToken().getValue()));
        assertThat(tkCacher.getTokenCache().get().getValue(), is(token.getValue()));
    }
}