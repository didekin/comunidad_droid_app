package com.didekindroid.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekin.http.oauth2.SpringOauthToken;
import com.didekindroid.exception.UiException;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekin.http.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.IS_GCM_TOKEN_SENT_TO_SERVER;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.IS_USER_REG;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.util.IoHelper.readStringFromFile;
import static com.didekindroid.util.IoHelper.writeFileFromString;
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

    /**
     *  Invariants:
     *  1. If a user is not registered (no record in database), she cannot be her gcm token recorded in database.
     *  2. If a user is registered, his gcm token can or cannot been updated in database. Gcm token is not updated
     *     when a user is registered. // TODO: cambiar esta posibilidad?
     * */
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

    public final BiFunction<Boolean, SpringOauthToken, Boolean> initTokenRegisterFunc = new InitializeIdentityFunc();

    final static class InitializeIdentityFunc implements BiFunction<Boolean, SpringOauthToken, Boolean> {
        @Override
        public Boolean apply(Boolean isLoginValid, SpringOauthToken token)
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

    public final Function<Boolean,Boolean> cleanTokenFunc = new CleanIdentityFunc();

    final static class CleanIdentityFunc implements Function<Boolean,Boolean> {
        @Override
        public Boolean apply(Boolean isDeletedUser)
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

    public final Consumer<SpringOauthToken> initTokenAction = new InitTokenAction();

    static class InitTokenAction implements Consumer<SpringOauthToken> {
        @Override
        public void accept(SpringOauthToken token)
        {
            TKhandler.initIdentityCache(token);
        }
    }

    public final Consumer<Integer> cleanTokenCacheAction = new CleanTokenCacheAction();

    static class CleanTokenCacheAction implements Consumer<Integer> {
        @Override
        public void accept(Integer modifiedUser)
        {
            if (modifiedUser > 0){
                TKhandler.cleanIdentityCache();
            }
        }
    }
}

