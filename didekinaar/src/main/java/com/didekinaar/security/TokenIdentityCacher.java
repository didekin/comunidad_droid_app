package com.didekinaar.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.AppInitializer.creator;
import static com.didekinaar.security.Oauth2DaoRemote.Oauth2;
import static com.didekinaar.security.TokenIdentityCacher.SharedPrefFiles.IS_GCM_TOKEN_SENT_TO_SERVER;
import static com.didekinaar.security.TokenIdentityCacher.SharedPrefFiles.IS_USER_REG;
import static com.didekinaar.security.TokenIdentityCacher.SharedPrefFiles.app_preferences_file;
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
public enum TokenIdentityCacher implements IdentityCacher {

    TKhandler,;

    public static final String refresh_token_filename = "tk_file";
    private final ThreadPoolExecutor tokenUpdater;
    private final AtomicReference<Future<SpringOauthToken>> cacheForToken = new AtomicReference<>();
    public final AtomicReference<SpringOauthToken> tokenInCache;
    private final File refreshTokenFile;
    private final Context context;

    TokenIdentityCacher()
    {
        tokenUpdater = new ThreadPoolExecutor(1, 1, 7L, SECONDS, new LinkedBlockingQueue<Runnable>(5));
        tokenUpdater.allowCoreThreadTimeOut(true);
        tokenUpdater.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        context = creator.get().getContext();
        refreshTokenFile = new File(context.getFilesDir(), refresh_token_filename);
        String refreshTokenValue = refreshTokenFile.exists() ? readStringFromFile(refreshTokenFile) : null;
        tokenInCache = (refreshTokenValue != null && !refreshTokenValue.isEmpty()) ?
                new AtomicReference<>(new SpringOauthToken(refreshTokenValue)) : new AtomicReference<SpringOauthToken>();
    }

    @Override
    public final void initIdentityCache(final SpringOauthToken springOauthToken)
    {
        Timber.d("initIdentityCache()");

        synchronized (refreshTokenFile) {
            cleanIdentityCache();
            writeFileFromString(springOauthToken.getRefreshToken().getValue(), refreshTokenFile);
        }
        tokenInCache.set(springOauthToken);
    }

    @Override
    public final void cleanIdentityCache()
    {
        Timber.d("cleanIdentityCache()");
        synchronized (refreshTokenFile) {
            refreshTokenFile.delete();
        }
        tokenInCache.set(null);
        cacheForToken.set(null);
    }

    /**
     * Preconditions:
     * 1. This method would be called mainly in an asyncTask thread or in a background thread.
     * However, it uses an ExecutorService instance to update asynchronously the tokens in cache.
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
            // It may throw a UiException(GENERIC_ERROR). See Oauth2DaoRemote.
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

    //    ...................  SHARED PREFERENCES .....................

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("TKhandler.isRegisteredUser()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_USER_REG, false);
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_USER_REG, isRegisteredUser);
        editor.apply();

        if (!isRegisteredUser) {
            updateIsGcmTokenSentServer(false);
        }
    }

    @Override
    public boolean isGcmTokenSentServer()
    {
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, false);
    }

    @Override
    public void updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, isSentToServer);
        editor.apply();
        Timber.d("updateIsGcmTokenSentServer(), iSentToServer= %b", isSentToServer);
    }

    public enum SharedPrefFiles {

        app_preferences_file,;

        public static final String IS_USER_REG = "TKhandler.isRegisteredUser";
        static final String IS_GCM_TOKEN_SENT_TO_SERVER = "isGcmTokenSentToServer";

        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(".").concat(this.name());
        }
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

    //  ======================================================================================
    //    .................................... FUNCTIONS .................................
    //  ======================================================================================

    public final Func2<Boolean, SpringOauthToken, Boolean> initTokenRegisterFunc = new InitializeIdentityFunc();

    final static class InitializeIdentityFunc implements Func2<Boolean, SpringOauthToken, Boolean> {
        @Override
        public Boolean call(Boolean isLoginValid, SpringOauthToken token)
        {
            boolean isUpdatedTokenData = isLoginValid && token != null;
            if (isUpdatedTokenData) {
                Timber.d("Updating token data ...");
                TKhandler.initIdentityCache(token);
                TKhandler.updateIsRegistered(true);
            }
            return isUpdatedTokenData;
        }
    }

    public final Func1<Boolean,Boolean> cleanTokenFunc = new CleanIdentityFunc();

    final static class CleanIdentityFunc implements Func1<Boolean,Boolean> {
        @Override
        public Boolean call(Boolean isDeletedUser)
        {
            if (isDeletedUser) {
                TKhandler.cleanIdentityCache();
                TKhandler.updateIsRegistered(false);
            }
            return isDeletedUser;
        }
    }

    //  ======================================================================================
    //    .................................... ACTIONS .................................
    //  ======================================================================================

    public final Action1<SpringOauthToken> initTokenAction = new InitTokenAction();

    static class InitTokenAction implements Action1<SpringOauthToken> {
        @Override
        public void call(SpringOauthToken token)
        {
            TKhandler.initIdentityCache(token);
        }
    }

    public final Action1<Integer> cleanTokenCacheAction = new CleanTokenCacheAction();

    static class CleanTokenCacheAction implements Action1<Integer> {
        @Override
        public void call(Integer modifiedUser)
        {
            if (modifiedUser > 0){
                TKhandler.cleanIdentityCache();
            }
        }
    }
}

