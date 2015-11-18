package com.didekindroid.common;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.oauth2.OauthToken;
import com.didekin.common.oauth2.OauthToken.AccessToken;
import com.didekindroid.common.utils.IoHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.didekindroid.common.TokenHandler.TKhandler;
import static com.didekindroid.common.TokenHandler.refresh_token_filename;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/08/15
 * Time: 17:08
 */
@RunWith(AndroidJUnit4.class)
public class TokenHandlerTest_B {

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

    /* This test requires initialization of the enum, so it run separately. */
    @Test
    public void testInstanceWithFile() throws IOException
    {
        // Precondition for initialization of TKhandler: file with refreshToken.

        IoHelper.writeFileFromString(accessToken.getRefreshToken().getValue(),
                new File(context.getFilesDir(), refresh_token_filename));
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenKey(), notNullValue());
        com.google.common.cache.Cache<String, AccessToken> tokenCache = TKhandler.getTokensCache();
        assertThat(tokenCache, notNullValue());
        // No accessToken entry in cache. The key is null.
        assertThat(tokenCache.getIfPresent(TKhandler.getRefreshTokenKey()), nullValue());
        assertThat(tokenCache.asMap().containsKey(TKhandler.getRefreshTokenKey()), is(false));
    }

    @After
    public void cleanFileToken()
    {
        TKhandler.cleanCacheAndBckFile();
    }
}

