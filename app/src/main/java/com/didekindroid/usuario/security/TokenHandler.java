package com.didekindroid.usuario.security;

import android.util.Log;
import com.didekin.security.OauthToken.AccessToken;
import com.didekindroid.ioutils.IoHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.didekin.security.OauthTokenHelper.HELPER;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.uiutils.UIutils.updateIsRegistered;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
/*
Un cambio en el userName o en el password implica un cambio en la composición del TokenRequester.
*/
public enum TokenHandler {

    TKhandler,;

    private static final String TAG = TokenHandler.class.getCanonicalName();
    public static final String refresh_token_filename = "tk_file";

    private final Cache<String, AccessToken> tokensCache;
    private String refreshTokenKey;
    private final File refreshTokenFile;

    TokenHandler()
    {
        tokensCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .<String, AccessToken>build();

        refreshTokenFile = new File(getContext().getFilesDir(), refresh_token_filename);
        refreshTokenKey = refreshTokenFile.exists() ? IoHelper.readStringFromFile(refreshTokenFile) : null;
    }

    /*  Called from the signup activity which does a password flow authentication:
        1. login.
        2. password or user modifications.

        Initialize the accessToken associated to the refreshToken and write to file this token.
    */
    public synchronized void initKeyCacheAndBackupFile(AccessToken accessToken)
    {
        Log.d(TAG, "initKeyCacheAndBackupFile()");

        IoHelper.writeFileFromString(checkNotNull(accessToken).getRefreshToken().getValue(), refreshTokenFile);
        if (refreshTokenKey != null) {
            tokensCache.invalidate(refreshTokenKey);
        }
        refreshTokenKey = accessToken.getRefreshToken().getValue();
        tokensCache.put(refreshTokenKey, accessToken);
    }

    public synchronized void cleanCacheAndBckFile()
    {
        Log.d(TAG, "cleanCacheAndBckFile()");

        TKhandler.getRefreshTokenFile().delete();
        TKhandler.getTokensCache().invalidateAll();
        TKhandler.updateRefreshToken(null);
    }

    public AccessToken getAccessTokenInCache()
    {
        Log.d(TAG, "getAccessTokenInCache()");

        // The user has not been registered.
        if (refreshTokenKey == null) {
            return null;
        }
        AccessToken accessToken = null;

        try {
            accessToken = tokensCache.get(refreshTokenKey, new Callable<AccessToken>() {

                @Override
                public AccessToken call() throws Exception
                {
                    return Oauth2.getRefreshUserToken(refreshTokenKey);
                }
            });
        } catch (ExecutionException e) {
            IoHelper.doRuntimeException(e, e.getLocalizedMessage());
        }
        return accessToken;
    }

    public void updateRefreshToken(String refreshToken)
    {
        Log.d(TAG, "updateRefreshToken()");
        refreshTokenKey = refreshToken;
        // TODO. implementar después de ver cómo conseguir un nuevo refreshToken de Spring Oauth.
    }

    //    ...................  UTILITIES .....................

    public String doBearerAccessTkHeader()
    {
        Log.d(TAG, "doBearerAccessTkHeader()");
        AccessToken accessToken = getAccessTokenInCache();
        if (accessToken != null) {
            return HELPER.doBearerAccessTkHeader(accessToken);
        }
        return null;
    }

//    .................... ACCESSOR METHODS .........................

    public Cache<String, AccessToken> getTokensCache()
    {
        return tokensCache;
    }

    public static String getRefresh_token_filename()
    {
        return refresh_token_filename;
    }

    public File getRefreshTokenFile()
    {
        return refreshTokenFile;
    }

    public String getRefreshTokenKey()
    {
        return refreshTokenKey;
    }
}

