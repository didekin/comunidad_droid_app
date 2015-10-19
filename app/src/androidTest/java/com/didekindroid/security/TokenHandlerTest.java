package com.didekindroid.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.OauthToken;
import com.didekin.retrofitcl.OauthToken.AccessToken;
import com.didekindroid.usuario.activity.utils.UsuarioTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.didekin.retrofitcl.OauthTokenHelper.HELPER;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.security.TokenHandler.refresh_token_filename;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_REAL_JUAN;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenHandlerTest {

    private Context context;
    private AccessToken accessToken;

    @Before
    public void getFixture()
    {
        context = InstrumentationRegistry.getTargetContext();
        accessToken = new AccessToken(
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef",
                new Timestamp(new Date().getTime() + 7200000),
                "bearer",
                new OauthToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", new Timestamp(new Date().getTime() + 7200000)),
                new String[]{"readwrite"}
        );
    }

    @Test
    public void testInstanceWithoutFile()
    {
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());
        com.google.common.cache.Cache<String, AccessToken> tokenCache = TKhandler.getTokensCache();
        assertThat(tokenCache, notNullValue());
        // No accessToken entry in cache. The key is null.
        assertThat(tokenCache.asMap().containsKey(TKhandler.getRefreshTokenKey()), is(false));
    }

    @Test
    public void testInitCacheAndFile_1() throws Exception
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        TKhandler.initKeyCacheAndBackupFile(accessToken);
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenKey(), is(accessToken.getRefreshToken().getValue()));
        assertThat(TKhandler.getTokensCache().asMap().containsKey(TKhandler.getRefreshTokenKey()), is(true));
        assertThat(TKhandler.getTokensCache().getIfPresent(TKhandler.getRefreshTokenKey()), notNullValue());
        assertThat(TKhandler.getTokensCache().getIfPresent(TKhandler.getRefreshTokenKey()).getValue(),
                is(accessToken.getValue()));
    }

    @Test
    public void testGetTkInCacheOnlyRefreshTk_1()
    {
        // No file with refreshToken.
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        AccessToken token = TKhandler.getAccessTokenInCache();
        assertThat(token, nullValue());
    }

    @Test
    public void testGetTkInCacheOnlyRefreshTk_2() throws IOException
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        // Registers user and initializes cache.
        // Case 1: there is a token in cache.
        UsuarioTestUtils.signUpAndUpdateTk(COMU_REAL_JUAN);
        AccessToken token_1 = TKhandler.getAccessTokenInCache();
        String accessToken_1 = token_1.getValue();
        assertThat(accessToken_1, not(isEmptyOrNullString()));
        String refreshToken_1 = token_1.getRefreshToken().getValue();
        assertThat(refreshToken_1, not(isEmptyOrNullString()));

        // Case 2: there is not token in cache, but there exists file with refreshToken.
        com.google.common.cache.Cache<String, AccessToken> tokenCache = TKhandler.getTokensCache();
        tokenCache.invalidate(refreshToken_1);
        assertThat(tokenCache.getIfPresent(refreshToken_1), nullValue());
        File refreshTkFile = new File(context.getFilesDir(), refresh_token_filename);
        assertThat(refreshTkFile.exists(), is(true));

        // The application must get another accessToken with a different refreshToken.
        AccessToken token_2 = TKhandler.getAccessTokenInCache();
        String accessToken_2 = token_2.getValue();
        assertThat(accessToken_2, not(isEmptyOrNullString()));
        assertThat(accessToken_2, not(equalTo(accessToken_1)));
        String refreshToken_2 = token_2.getRefreshToken().getValue();
        assertThat(refreshToken_2, not(isEmptyOrNullString()));
        assertThat(refreshToken_2, not(is(refreshToken_1)));

        // User clean up.
        assertThat(ServOne.deleteUser(), is(true));
    }

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        String bearerTk = HELPER.doBearerAccessTkHeader(accessToken);
        assertThat(bearerTk, equalTo("Bearer " + accessToken.getValue()));
    }

    @Test
    public void testDoBearerAccessTkHeaderNull() throws Exception
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        String bearerHeader = TKhandler.doBearerAccessTkHeader();
        assertThat(bearerHeader, nullValue());
    }

    @Test
    public void testCleanCacheAndBckFile()
    {
        // Precondition for initialization of TKhandler: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        // Preconditions: the user is registered.
        TKhandler.initKeyCacheAndBackupFile(accessToken);
        assertThat(TKhandler.getRefreshTokenKey(), notNullValue());

        TKhandler.cleanCacheAndBckFile();
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

    }

    @After
    public void cleanFileToken()
    {
        TKhandler.cleanCacheAndBckFile();
    }

//    .................... UTILITIES .......................
}