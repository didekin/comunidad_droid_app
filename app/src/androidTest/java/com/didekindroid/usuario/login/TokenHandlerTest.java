package com.didekindroid.usuario.login;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.didekindroid.common.IoHelper;
import com.didekindroid.usuario.DataUsuarioTestUtils;
import com.didekindroid.usuario.login.dominio.AccessToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static com.didekindroid.usuario.login.TokenHandler.TKhandler;
import static com.didekindroid.usuario.login.TokenHandler.refresh_token_filename;
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
        accessToken = new AccessToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", 239,
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", "readwrite", "bearer");
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
        assertThat(TKhandler.getRefreshTokenKey(), is(accessToken.getRefresh_token()));
        assertThat(TKhandler.getTokensCache().asMap().containsKey(TKhandler.getRefreshTokenKey()), is(true));
        assertThat(TKhandler.getTokensCache().getIfPresent(TKhandler.getRefreshTokenKey()), notNullValue());
        assertThat(TKhandler.getTokensCache().getIfPresent(TKhandler.getRefreshTokenKey()).getAccess_token(),
                is(accessToken.getAccess_token()));
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
        DataUsuarioTestUtils.insertOneUserOneComu();
        AccessToken token_1 = TKhandler.getAccessTokenInCache();
        String accessToken_1 = token_1.getAccess_token();
        assertThat(accessToken_1, not(isEmptyOrNullString()));
        String refreshToken_1 = token_1.getRefresh_token();
        assertThat(refreshToken_1, not(isEmptyOrNullString()));

        // Case 2: there is not token in cache, but there exists file with refreshToken.
        com.google.common.cache.Cache<String, AccessToken> tokenCache = TKhandler.getTokensCache();
        tokenCache.invalidate(refreshToken_1);
        assertThat(tokenCache.getIfPresent(refreshToken_1), nullValue());
        File refreshTkFile = new File(context.getFilesDir(), refresh_token_filename);
        assertThat(refreshTkFile.exists(), is(true));

        // The application must get another accessToken with the same refreshToken.
        AccessToken token_2 = TKhandler.getAccessTokenInCache();
        String accessToken_2 = token_2.getAccess_token();
        assertThat(accessToken_2, not(isEmptyOrNullString()));
        assertThat(accessToken_2, not(equalTo(accessToken_1)));
        String refreshToken_2 = token_2.getRefresh_token();
        assertThat(refreshToken_2, not(isEmptyOrNullString()));
        assertThat(refreshToken_2, equalTo(refreshToken_1));

        // User clean up.
        assertThat(ServOne.deleteUser(), is(true));
    }

    @Test
    public void testDoBearerAccessTkHeader()
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        String bearerTk = TKhandler.doBearerAccessTkHeader(accessToken);
        assertThat(bearerTk, equalTo("Bearer " + accessToken.getAccess_token()));
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
    public void testCleanUp()
    {
        // Precondition for initialization of the enum: no file with refreshToken.
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());

        if (TKhandler.getRefreshTokenFile().exists()) {
            TKhandler.getRefreshTokenFile().delete();
        }
        TKhandler.getTokensCache().invalidateAll();
//        TKhandler.updateRefreshToken(null);

        assertThat(TKhandler.getRefreshTokenFile().exists(), is(false));
        assertThat(TKhandler.getRefreshTokenKey(), nullValue());
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());
    }

    @After
    public void cleanFileToken()
    {
        if (TKhandler.getRefreshTokenFile().exists()) {
            TKhandler.getRefreshTokenFile().delete();
        }
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }

//    .................... UTILITIES .......................
}