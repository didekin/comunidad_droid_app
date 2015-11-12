package com.didekindroid.common;

import com.didekin.common.oauth2.OauthToken;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 11/11/15
 * Time: 10:41
 */

public class TokenHandlerTest_C {

    @Test
    public void getTokenInCache_1() throws ExecutionException
    {
        Cache<String, OauthToken.AccessToken> tokensCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .build();

        OauthToken.AccessToken accessToken = tokensCache.get("hola", new Callable<OauthToken.AccessToken>() {

            @Override
            public OauthToken.AccessToken call() throws Exception
            {
                return Oauth2.getRefreshUserToken("hola");
            }
        });

        assertThat(accessToken,nullValue());
    }

}