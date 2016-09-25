package com.didekindroid.common.activity;

import com.didekin.oauth2.SpringOauthToken;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.DidekindroidApp.getContext;
import static com.didekindroid.common.utils.IoHelper.readStringFromFile;
import static com.didekindroid.common.utils.IoHelper.writeFileFromString;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */

/**
 *
 */
public enum TokenHandler {

    TKhandler,;

    public static final String refresh_token_filename = "tk_file";
    private final ExecutorService tokenUpdater = Executors.newSingleThreadExecutor();
    private final AtomicReference<Future<SpringOauthToken>> cacheForToken = new AtomicReference<>();
    final AtomicReference<SpringOauthToken> tokenInCache;
    private final File refreshTokenFile;

    TokenHandler()
    {
        refreshTokenFile = new File(getContext().getFilesDir(), refresh_token_filename);
        String refreshTokenValue = refreshTokenFile.exists() ? readStringFromFile(refreshTokenFile) : null;
        tokenInCache =  (refreshTokenValue != null && !refreshTokenValue.isEmpty()) ?
                new AtomicReference<>(new SpringOauthToken(refreshTokenValue)) :  new AtomicReference<SpringOauthToken>();
    }

    public final void initTokenAndBackupFile(final SpringOauthToken springOauthToken)
    {
        Timber.d("initTokenAndBackupFile()");

        cleanTokenAndBackFile();
        synchronized (refreshTokenFile) {
            tokenInCache.set(springOauthToken);
            writeFileFromString(springOauthToken.getRefreshToken().getValue(), refreshTokenFile);
        }
    }

    public final void cleanTokenAndBackFile()
    {
        Timber.d("cleanTokenAndBackFile()");
        synchronized (refreshTokenFile) {
            refreshTokenFile.delete();
            tokenInCache.set(null);
            cacheForToken.set(null);
        }
    }

    /**
     * Preconditions:
     * 1. This method would be called mainly in an asyncTask thread or in a background thread.
     *    However, it uses an ExecutorService instance to update asynchronously the tokens in cache.
     */
    public final SpringOauthToken getAccessTokenInCache() throws UiException
    {
        Timber.d("getAccessTokenInCache()");

        if (tokenInCache.get() == null) {
            return null;
        }
        synchronized (this) {
            if (tokenInCache.get().getValue() != null) {
                return tokenInCache.get();
            }
        }

        Future<SpringOauthToken> futureInCache = cacheForToken.get();

        if (futureInCache == null) {

            Callable<SpringOauthToken> cacheUpdater = new Callable<SpringOauthToken>() {
                @Override
                public SpringOauthToken call() throws UiException
                {
                    return Oauth2.getRefreshUserToken(tokenInCache.get().getRefreshToken().getValue());
                }
            };

            Future<SpringOauthToken> futureToCache = new FutureTask<>(cacheUpdater);
            futureInCache = cacheForToken.getAndSet(futureToCache);
            if (futureInCache == null) {
                futureInCache = tokenUpdater.submit(cacheUpdater);
            }
        }
        try {
            tokenInCache.set(futureInCache.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            futureInCache.cancel(true);
            cacheForToken.set(null);
        } catch (ExecutionException e) {
            return catchExecutionException(e);
        }
        return tokenInCache.get();
    }

    //    ...................  UTILITIES .....................

    public String doBearerAccessTkHeader() throws UiException
    {
        Timber.d("doBearerAccessTkHeader()");
        SpringOauthToken springOauthToken = getAccessTokenInCache();
        if (springOauthToken != null) {
            return HELPER.doBearerAccessTkHeader(springOauthToken);
        }
        return null;
    }

    private SpringOauthToken catchExecutionException(ExecutionException e) throws UiException
    {
        cacheForToken.set(null);
        Throwable cause = e.getCause();
        if (cause instanceof UiException) {
            throw (UiException) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            throw new IllegalStateException("What is this checkedException?", cause);
        }
    }

//    .................... ACCESSOR METHODS .........................

    public SpringOauthToken getTokenInCache()
    {
        return tokenInCache.get();
    }

    public File getRefreshTokenFile()
    {
        return refreshTokenFile;
    }

    public String getRefreshTokenValue()
    {
        return tokenInCache.get() != null ? tokenInCache.get().getRefreshToken().getValue() : null;
    }

    AtomicReference<Future<SpringOauthToken>> getCacheForToken()
    {
        return cacheForToken;
    }
}

