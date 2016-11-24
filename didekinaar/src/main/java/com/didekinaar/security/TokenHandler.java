package com.didekinaar.security;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiAarException;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.utils.IoHelper.readStringFromFile;
import static com.didekinaar.utils.IoHelper.writeFileFromString;
import static java.util.concurrent.TimeUnit.SECONDS;

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
    private final ThreadPoolExecutor tokenUpdater;
    private final AtomicReference<Future<SpringOauthToken>> cacheForToken = new AtomicReference<>();
    final AtomicReference<SpringOauthToken> tokenInCache;
    private final File refreshTokenFile;

    TokenHandler()
    {
        tokenUpdater = new ThreadPoolExecutor(1, 1, 7L, SECONDS, new LinkedBlockingQueue<Runnable>(5));
        tokenUpdater.allowCoreThreadTimeOut(true);
        tokenUpdater.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        refreshTokenFile = new File(creator.get().getContext().getFilesDir(), refresh_token_filename);
        String refreshTokenValue = refreshTokenFile.exists() ? readStringFromFile(refreshTokenFile) : null;
        tokenInCache =  (refreshTokenValue != null && !refreshTokenValue.isEmpty()) ?
                new AtomicReference<>(new SpringOauthToken(refreshTokenValue)) :  new AtomicReference<SpringOauthToken>();
    }

    public final void initTokenAndBackupFile(final SpringOauthToken springOauthToken)
    {
        Timber.d("initTokenAndBackupFile()");

        synchronized (refreshTokenFile) {
            cleanTokenAndBackFile();
            writeFileFromString(springOauthToken.getRefreshToken().getValue(), refreshTokenFile);
        }
        tokenInCache.set(springOauthToken);
    }

    public final void cleanTokenAndBackFile()
    {
        Timber.d("cleanTokenAndBackFile()");
        synchronized (refreshTokenFile) {
            refreshTokenFile.delete();
        }
        tokenInCache.set(null);
        cacheForToken.set(null);
    }

    /**
     * Preconditions:
     * 1. This method would be called mainly in an asyncTask thread or in a background thread.
     *    However, it uses an ExecutorService instance to update asynchronously the tokens in cache.
     */
    public final SpringOauthToken getAccessTokenInCache() throws UiAarException
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
                public SpringOauthToken call() throws UiAarException
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

    public String doBearerAccessTkHeader() throws UiAarException
    {
        Timber.d("doBearerAccessTkHeader()");
        SpringOauthToken springOauthToken = getAccessTokenInCache();
        if (springOauthToken != null) {
            return HELPER.doBearerAccessTkHeader(springOauthToken);
        }
        return null;
    }

    private SpringOauthToken catchExecutionException(ExecutionException e) throws UiAarException
    {
        cacheForToken.set(null);
        Throwable cause = e.getCause();
        if (cause instanceof UiAarException) {
            throw (UiAarException) cause;
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

