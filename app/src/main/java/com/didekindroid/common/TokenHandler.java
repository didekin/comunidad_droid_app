package com.didekindroid.common;

import android.util.Log;

import com.didekin.common.oauth2.OauthToken.AccessToken;
import com.didekindroid.R;
import com.didekindroid.common.utils.IoHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.didekin.common.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.UiException.UiAction.LOGIN;
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
    private volatile String refreshTokenKey;
    private final File refreshTokenFile;

    TokenHandler()
    {
        tokensCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(120, TimeUnit.MINUTES)
                .build();

        refreshTokenFile = new File(getContext().getFilesDir(), refresh_token_filename);
        refreshTokenKey = refreshTokenFile.exists() ? IoHelper.readStringFromFile(refreshTokenFile) : null;
    }

    public final synchronized void initKeyCacheAndBackupFile(AccessToken accessToken)
    {
        Log.d(TAG, "initKeyCacheAndBackupFile()");

        cleanCacheAndBckFile();

        IoHelper.writeFileFromString(checkNotNull(accessToken).getRefreshToken().getValue(), refreshTokenFile);

        refreshTokenKey = accessToken.getRefreshToken().getValue();
        tokensCache.put(refreshTokenKey, accessToken);
    }

    public final synchronized void cleanCacheAndBckFile()
    {
        Log.d(TAG, "cleanCacheAndBckFile()");

        //noinspection ResultOfMethodCallIgnored
        refreshTokenFile.delete();
        tokensCache.invalidateAll();
        refreshTokenKey = null;
    }

    public final AccessToken getAccessTokenInCache() throws UiException
    {
        Log.d(TAG, "getAccessTokenInCache()");

        final String refreshTokenKeyLocal = refreshTokenKey;

        if (refreshTokenKeyLocal == null) {
            return null;
        }
        AccessToken accessToken;

        try {
            accessToken = tokensCache.get(refreshTokenKeyLocal, new Callable<AccessToken>() {

                @Override
                public AccessToken call() throws Exception
                {
                    return Oauth2.getRefreshUserToken(refreshTokenKeyLocal);
                }
            });
        } catch (ExecutionException e) {
            throw new UiException(LOGIN, R.string.user_without_signedUp, null);
        }

        return accessToken;
    }

    //    ...................  UTILITIES .....................

    public String doBearerAccessTkHeader() throws UiException
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

    public File getRefreshTokenFile()
    {
        return refreshTokenFile;
    }

    public String getRefreshTokenKey()
    {
        return refreshTokenKey;
    }
}

