package com.didekindroid.security;

import android.content.Context;
import android.content.SharedPreferences;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.OauthTokenReactor.oauthTokenFromRefreshTk;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.IS_GCM_TOKEN_SENT_TO_SERVER;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.IS_USER_REG;
import static com.didekindroid.security.TokenIdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.usuario.UsuarioAssertionMsg.identity_token_should_be_notnull;
import static com.didekindroid.usuario.UsuarioAssertionMsg.updateIdentityToken_should_be_completed;
import static com.didekindroid.util.IoHelper.readStringFromFile;
import static com.didekindroid.util.IoHelper.writeFileFromString;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.oauth2.OauthTokenHelper.HELPER;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class TokenIdentityCacher implements IdentityCacher {

    public static final IdentityCacher TKhandler = new TokenIdentityCacher(creator.get().getContext());

    //  ======================================================================================
    //    .................................... FUNCTIONS .................................
    //  ======================================================================================

    static final BiFunction<Boolean, SpringOauthToken, Boolean> initTokenAndRegisterFunc = new BiFunction<Boolean, SpringOauthToken, Boolean>() {
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
    };

    public static final Function<Boolean, Boolean> cleanTokenAndUnregisterFunc = new Function<Boolean, Boolean>() {
        @Override
        public Boolean apply(Boolean isDeletedUser)
        {
            if (isDeletedUser) {
                TKhandler.cleanIdentityCache();
                TKhandler.updateIsRegistered(false);
            }
            return isDeletedUser;
        }
    };

    //  ======================================================================================
    //    .................................... ACTIONS .................................
    //  ======================================================================================

    public static final Consumer<Integer> cleanTokenCacheAction = new Consumer<Integer>() {
        @Override
        public void accept(Integer modifiedUser)
        {
            if (modifiedUser > 0) {
                TKhandler.cleanIdentityCache();
            }
        }
    };

    public static final Consumer<Boolean> cleanTkCacheActionBoolean = new Consumer<Boolean>() {
        @Override
        public void accept(Boolean isToClean) throws Exception
        {
            cleanTokenCacheAction.accept(isToClean ? 1 : 0);
        }
    };

    static final Consumer<SpringOauthToken> initTokenAction = new Consumer<SpringOauthToken>() {
        @Override
        public void accept(SpringOauthToken token)
        {
            TKhandler.initIdentityCache(token);
        }
    };

    //  ======================================================================================
    //    ............................... TOKEN CACHE .................................
    //  ======================================================================================

    static final String refresh_token_filename = "tk_file";
    private final AtomicReference<SpringOauthToken> tokenCache;
    private final File refreshTokenFile;
    private final Context context;

    private TokenIdentityCacher(Context context)
    {
        this.context = context;
        refreshTokenFile = new File(this.context.getFilesDir(), refresh_token_filename);
        String refreshTokenValue = refreshTokenFile.exists() ? readStringFromFile(refreshTokenFile) : null;
        tokenCache = (refreshTokenValue != null && !refreshTokenValue.isEmpty()) ?
                new AtomicReference<>(new SpringOauthToken(refreshTokenValue)) : new AtomicReference<SpringOauthToken>();
    }

    /**
     * Preconditions:
     * 1. Parameter is not null.
     * Postconditions:
     * 1. A new file is written with the refresh token in the parameter.
     * 2. Access token in cache is initialized.
     */
    @Override
    public final void initIdentityCache(final SpringOauthToken springOauthToken)
    {
        Timber.d("initIdentityCache()");
        assertTrue(springOauthToken != null, identity_token_should_be_notnull);

        synchronized (refreshTokenFile) {
            cleanIdentityCache();
            writeFileFromString(springOauthToken.getRefreshToken().getValue(), refreshTokenFile);
        }
        tokenCache.set(springOauthToken);
    }

    @Override
    public final void cleanIdentityCache()
    {
        Timber.d("cleanIdentityCache()");
        synchronized (refreshTokenFile) {
            refreshTokenFile.delete();
        }
        tokenCache.set(null);
    }

    /**
     * Preconditions:
     * 1. This method will be called mainly in an asyncTask thread or in a background thread. It blocks until
     * a remote token is retrieved, if there exists a refresh token file in local.
     * Postconditions:
     * 1. If the cache has not been initialized (tokenCache == null), it returns null.
     * 2. If there is an access token in cache, it is returned.
     * 3. If tokenCache != null, but tokenCache.get().getValue() is null (no access token in cache,
     * but there exists a refresh token file), the access token is remotely retrieved and updated in
     * cache.
     *
     * @return null if:
     * 1. The token in cache is null.
     * 2. The synchronous call to update remotely the access token times out.
     */
    @Override
    public final SpringOauthToken getAccessTokenInCache() throws UiException
    {
        Timber.d("getAccessTokenInCache()");

        if (tokenCache.get() == null) {
            return null;
        }
        synchronized (this) {
            if (tokenCache.get().getValue() != null) {
                return tokenCache.get();
            }
        }

        try {
            boolean isCompleted = oauthTokenFromRefreshTk(getRefreshTokenValue()).blockingAwait(5L, SECONDS);
            assertTrue(isCompleted, updateIdentityToken_should_be_completed);
        } catch (Exception e) {
            TKhandler.cleanIdentityCache();
            throw new UiException(new ErrorBean(TOKEN_NULL));
        }
        return tokenCache.get();
    }

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("TKhandler.isRegisteredUser()");

        SharedPreferences sharedPref = context.getSharedPreferences
                (app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IS_USER_REG, false);
    }

    /**
     * Invariants:
     * 1. If a user is not registered (no record in database), she cannot be her gcm token recorded in database.
     * 2. If a user is registered, his gcm token can or cannot been updated in database. Gcm token is not updated
     * when a user is registered. // TODO: cambiar esta posibilidad?
     */
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

    //  ======================================================================================
    //    .................................... UTILITIES .................................
    //  ======================================================================================

    @Override
    public String doHttpAuthHeaderFromTkInCache() throws UiException
    {
        Timber.d("doHttpAuthHeaderFromTkInCache()");
        SpringOauthToken springOauthToken = getAccessTokenInCache();
        if (springOauthToken != null) {
            return HELPER.doBearerAccessTkHeader(springOauthToken);
        }
        return null;
    }

    //  ======================================================================================
    //    .................................... ACCESSORS .................................
    /*  ======================================================================================*/

    @Override
    public AtomicReference<SpringOauthToken> getTokenCache()
    {
        return tokenCache;
    }

    @Override
    public File getRefreshTokenFile()
    {
        return refreshTokenFile;
    }

    @Override
    public Context getContext()
    {
        return context;
    }

    @Override
    public String getRefreshTokenValue()
    {
        return tokenCache.get() != null ? tokenCache.get().getRefreshToken().getValue() : null;
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
}

