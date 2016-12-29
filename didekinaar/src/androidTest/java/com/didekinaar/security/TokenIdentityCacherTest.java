package com.didekinaar.security;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.testutil.AarTestUtil.doSpringOauthToken;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
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
public class TokenIdentityCacherTest {

    protected Context context;

    @Before
    public void getFixture()
    {
        cleanWithTkhandler();
        context = getTargetContext();
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
    public void testDoBearerAccessTkHeaderNull() throws Exception
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenValue(), nullValue());

        String bearerHeader = TKhandler.doBearerAccessTkHeader();
        assertThat(bearerHeader, nullValue());
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

//    .................... FUNCTIONS .......................

    @Test
    public void testInitTokenFunction_1() throws UiException
    {
        initTokenHelper();
    }

    @Test
    public void testInitTokenFunction_2() throws UiException
    {
        SpringOauthToken token = doSpringOauthToken();
        TKhandler.initTokenRegisterFunc.call(false, token);
        assertThat(TKhandler.getAccessTokenInCache(), is(nullValue()));
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenFunction_1() throws UiException
    {
        initTokenHelper();
        TKhandler.cleanTokenFunc.call(true);
        assertThat(TKhandler.getAccessTokenInCache(), is(nullValue()));
        assertThat(TKhandler.isRegisteredUser(), is(false));
    }

    @Test
    public void testCleanTokenFunction_2() throws UiException
    {
        initTokenHelper();
        TKhandler.cleanTokenFunc.call(false);
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }

    // ............................... HELPER ..............................

    private void initTokenHelper() throws UiException
    {
        SpringOauthToken token = doSpringOauthToken();
        TKhandler.initTokenRegisterFunc.call(true, token);
        assertThat(TKhandler.getAccessTokenInCache(), is(token));
        assertThat(TKhandler.isRegisteredUser(), is(true));
    }
}