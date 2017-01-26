package com.didekindroid.security;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.http.oauth2.SpringOauthToken;
import com.didekindroid.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekin.http.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.security.TokenIdentityCacher.cleanTokenCacheAction;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAction;
import static com.didekindroid.security.TokenIdentityCacher.initTokenAndRegisterFunc;
import static com.didekindroid.testutil.SecurityTestUtils.doSpringOauthToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.util.IoHelper.readStringFromFile;
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

    @Before
    public void getFixture()
    {
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Test
    public void testInitTokenAndBackFile() throws Exception
    {
        // Precondition: no file with refreshToken. We receive a fully initialized token instance.
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initIdentityCache(springOauthToken);
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenValue(), is(springOauthToken.getRefreshToken().getValue()));
        assertThat(TKhandler.getAccessTokenInCache(), is(springOauthToken));
    }

    @Test
    public void testCleanCacheAndBckFile() throws UiException
    {
        // Preconditions: there exist token data and file.
        SpringOauthToken springOauthToken = doSpringOauthToken();
        TKhandler.initIdentityCache(springOauthToken);

        TKhandler.cleanIdentityCache();
        // Assertions.
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
    }

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));

        SpringOauthToken springOauthToken = doSpringOauthToken();
        String bearerTk = HELPER.doBearerAccessTkHeader(springOauthToken);
        assertThat(bearerTk, equalTo("Bearer " + springOauthToken.getValue()));
    }

    @Test
    public void testUpdateIsRegistered() throws Exception
    {
        TKhandler.updateIsRegistered(false);
        assertThat(TKhandler.isRegisteredUser(), is(false));
        TKhandler.updateIsRegistered(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @Test
    public void updateIsGcmTokenSentServer() throws Exception
    {
        assertThat(TKhandler.isGcmTokenSentServer(), is(false));
        TKhandler.updateIsGcmTokenSentServer(true);
        assertThat(TKhandler.isGcmTokenSentServer(), is(true));
        TKhandler.updateIsGcmTokenSentServer(false);
        assertThat(TKhandler.isGcmTokenSentServer(), is(false));
    }

//    .................... ACTIONS .......................

    @Test
    public void testInitTokenAction() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAction.accept(token);
        checkSiCacheAndFile(token);
    }

    @Test
    public void testCleanTokenCacheAction() throws Exception
    {
        // No user registered. We test for the non-nullity of refreshTokenFile.
        cleanTokenCacheAction.accept(1);
        checkNoCacheAndFile();
    }

//    .................... FUNCTIONS .......................

    @Test
    public void testInitTokenAndRegisterFunc_1() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        assertThat(initTokenAndRegisterFunc.apply(true, token), is(true));
        checkSiCacheAndFile(token);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    @Test
    public void testInitTokenAndRegisterFunc_2() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAndRegisterFunc.apply(false, token);
        checkNoCacheAndFile();
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_1() throws Exception
    {
        initTokenHelper();
        cleanTokenAndUnregisterFunc.apply(true);
        checkNoCacheAndFile();
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenAndUnregisterFunc_2() throws Exception
    {
        SpringOauthToken token = initTokenHelper();
        cleanTokenAndUnregisterFunc.apply(false);
        checkSiCacheAndFile(token);
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    // ............................... HELPER ..............................

    private SpringOauthToken initTokenHelper() throws Exception
    {
        SpringOauthToken token = doSpringOauthToken();
        initTokenAndRegisterFunc.apply(true, token);
        assertThat(TKhandler.getAccessTokenInCache(), is(token));
        assertThat(TKhandler.isRegisteredUser(), is(true));
        return token;
    }

    private void checkNoCacheAndFile() throws UiException
    {
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), is(nullValue()));
    }

    private void checkSiCacheAndFile(SpringOauthToken token)
    {
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(readStringFromFile(TKhandler.getRefreshTokenFile()), is(token.getRefreshToken().getValue()));
        assertThat(TKhandler.getTokenCache().get().getValue(), is(token.getValue()));
    }
}