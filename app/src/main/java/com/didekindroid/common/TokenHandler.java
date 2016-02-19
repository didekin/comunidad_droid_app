package com.didekindroid.common;

import android.util.Log;

import com.didekin.common.oauth2.OauthToken.AccessToken;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.IoHelper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.didekin.common.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
/**
 *  Synchronization policy:
 *  - Synchronization of cache (put/get/invalidate) is delegated to the implementation.
 *  - To avoid a race condition 'locally' on the value of the refreshTokenKey, its value
 *    is thread confined at the beginning of the getter method in a local variable. It is also
 *    declared volatile.
 *  - The invariant 'a refreshTokenFile not empty implies a refreshTokenKey not null' is maintained
 *    synchronizing, on the intrinsic lock of the object, the method cleanCacheBckFile().
 *  - The invariants 'the same refreshToken is assigned to refreshTokenKey and written to a file' and
 *    'the refreshTokenKey and the accessToken in cache both corresponds to the same instance of
 *    an AccessToken' assignment, file writting and caching are synchronized on the intrinsic lock
 *    of the object.
 * */
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

    public final void initKeyCacheAndBackupFile(final AccessToken accessToken)
    {
        Log.d(TAG, "initKeyCacheAndBackupFile()");

        // Not stricty necessary; just convenient.
        cleanCacheAndBckFile();

        synchronized (this) {
            refreshTokenKey = checkNotNull(accessToken).getRefreshToken().getValue();
            IoHelper.writeFileFromString(refreshTokenKey, refreshTokenFile);
            tokensCache.put(refreshTokenKey, accessToken);
        }
    }

    public final synchronized void cleanCacheAndBckFile()
    {
        Log.d(TAG, "cleanCacheAndBckFile()");

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
            throw new UiException(null);
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

