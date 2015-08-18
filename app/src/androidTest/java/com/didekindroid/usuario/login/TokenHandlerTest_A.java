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
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/08/15
 * Time: 17:08
 */
@RunWith(AndroidJUnit4.class)
public class TokenHandlerTest_A {

    private Context context;
    private AccessToken accessToken;

    @Before
    public void getFixture()
    {
        context = InstrumentationRegistry.getTargetContext();
        accessToken = new AccessToken("50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", 239,
                "50d3cdaa-0d2e-4cfd-b259-82b3a0b1edef", "readwrite", "bearer");
    }

    /* This test requires initialization of the enum, so it run separately. */
    @Test
    public void testInstanceWithFile() throws IOException
    {
        IoHelper.writeFileFromString(accessToken.getRefresh_token(),
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

