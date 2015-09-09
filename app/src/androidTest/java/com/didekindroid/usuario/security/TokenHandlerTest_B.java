package com.didekindroid.usuario.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.security.OauthToken;
import com.didekin.security.OauthToken.AccessToken;
import com.didekindroid.ioutils.IoHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import static com.didekindroid.usuario.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.security.TokenHandler.refresh_token_filename;
import static org.hamcrest.CoreMatchers.*;
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
        IoHelper.writeFileFromString(accessToken.getRefreshToken().getValue(),
                new File(context.getFilesDir(), refresh_token_filename));
        assertThat(TKhandler.getRefreshTokenFile(), notNullValue());
        assertThat(TKhandler.getRefreshTokenFile().exists(), is(true));
        assertThat(TKhandler.getRefreshTokenKey(), notNullValue());
        com.google.common.cache.Cache<String, AccessToken> tokenCache = TKhandler.getTokensCache();
        assertThat(tokenCache, notNullValue());
        // No accessToken entry in cache. The key is not null.
        assertThat(tokenCache.getIfPresent(TKhandler.getRefreshTokenKey()), nullValue());
        assertThat(tokenCache.asMap().containsKey(TKhandler.getRefreshTokenKey()), is(false));
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
}

