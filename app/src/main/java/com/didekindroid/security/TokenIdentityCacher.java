package com.didekindroid.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

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
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.IS_USER_REG;
import static com.didekindroid.security.IdentityCacher.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.usuario.UsuarioAssertionMsg.identity_token_should_be_notnull;
import static com.didekindroid.usuario.firebase.CtrlerFirebaseTokenIf.IS_GCM_TOKEN_SENT_TO_SERVER;
import static com.didekindroid.util.IoHelper.readStringFromFile;
import static com.didekindroid.util.IoHelper.writeFileFromString;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.oauth2.OauthTokenHelper.HELPER;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class TokenIdentityCacher implements IdentityCacher {

    public static final IdentityCacher TKhandler = new TokenIdentityCacher(creator.get().getContext());

    //  ======================================================================================
    //    .................................... ACTIONS AND FUNCTIONS .................................
    //  ======================================================================================

    public static final Function<Boolean, Boolean> cleanTokenAndUnregisterFunc = new Function<Boolean, Boolean>() {

        @Override
        public Boolean apply(Boolean isDeletedUser)
        {
            Timber.d("cleanTokenAndUnregisterFunc.apply()");

            if (isDeletedUser) {
                TKhandler.cleanIdentityCache();
                TKhandler.updateIsRegistered(false);
            }
            return isDeletedUser;
        }
    };

    public static final Consumer<Boolean> cleanTkCacheConsumer = new Consumer<Boolean>() {
        @Override
        public void accept(Boolean isUserModified)
        {
            if (isUserModified) {
                TKhandler.cleanIdentityCache();
            }
        }
    };

    static final Consumer<SpringOauthToken> initTokenAction = new Consumer<SpringOauthToken>() {
        @Override
        public void accept(SpringOauthToken token)
        {
            Timber.d("accept(), Thread: %s", Thread.currentThread().getName());
            TKhandler.initIdentityCache(token);
        }
    };

    static final BiFunction<Boolean, SpringOauthToken, Boolean> initTokenAndRegisterFunc
            = new BiFunction<Boolean, SpringOauthToken, Boolean>() {

        @Override
        public Boolean apply(Boolean isLoginValid, SpringOauthToken token)
        {
            Timber.d("initTokenAndRegisterFunc.apply()");

            boolean isUpdatedTokenData = isLoginValid && token != null;
            if (isUpdatedTokenData) {
                Timber.d("Updating token data ...");
                TKhandler.initIdentityCache(token);
                TKhandler.updateIsRegistered(true);
            }
            return isUpdatedTokenData;
        }
    };
    static final Consumer<SpringOauthToken> initTokenUpdateRegisterAction = new Consumer<SpringOauthToken>() {
        @Override
        public void accept(SpringOauthToken token)
        {
            Timber.d("accept(), Thread: %s", Thread.currentThread().getName());
            TKhandler.initIdentityCache(token);
            TKhandler.updateIsRegistered(true);
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
        this(new File(context.getFilesDir(), refresh_token_filename), context);
    }

    /**
     * It allows for a more friendly injection constructor.
     */
    private TokenIdentityCacher(File refreshTkFile, Context inContext)
    {
        Timber.d("TokenIdentityCacher(File refreshTkFile, Context inContext)");
        refreshTokenFile = refreshTkFile;
        context = inContext;
        String refreshTokenValue = refreshTokenFile.exists() ? readStringFromFile(refreshTokenFile) : null;
        tokenCache = (refreshTokenValue != null && !refreshTokenValue.isEmpty()) ?
                new AtomicReference<>(new SpringOauthToken(refreshTokenValue)) :
                new AtomicReference<SpringOauthToken>();
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

    //  ======================================================================================
    //    ............................... SHARED PREFERENCES .................................
    //  ======================================================================================

    @Override
    public boolean isRegisteredUser()
    {
        SharedPreferences sharedPref = context.getSharedPreferences
                (app_preferences_file.toString(), MODE_PRIVATE);
        boolean isRegistered = sharedPref.getBoolean(IS_USER_REG, false);
        Timber.d("isRegisteredUser() = %b", isRegistered);
        return isRegistered;
    }

    /**
     * Invariants:
     * 1. If a user is not registered (no record in database), she cannot be her gcm token recorded in database.
     * 2. If a user is registered, his gcm token can or cannot been updated in database. Gcm token is not updated
     * when a user is registered.
     */
    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_USER_REG, isRegisteredUser);
        if (!isRegisteredUser) {
            editor.putBoolean(IS_GCM_TOKEN_SENT_TO_SERVER, false);
        }
        editor.apply();
    }

    //  ======================================================================================
    //    .................................... UTILITIES .................................
    //  ======================================================================================

    @Nullable
    private String doHttpAuthHeader(SpringOauthToken oauthToken)
    {
        Timber.d("doHttpAuthHeader(token)");
        if (oauthToken != null && !oauthToken.getValue().isEmpty()) {
            return HELPER.doBearerAccessTkHeader(oauthToken);
        }
        return null;
    }

    @Override
    public String checkBearerTokenInCache() throws UiException
    {
        Timber.d("checkBearerTokenInCache()");
        return checkBearerToken(tokenCache.get());
    }

    @Override
    public String checkBearerToken(SpringOauthToken oauthToken) throws UiException
    {
        Timber.d("checkBearerTokenInCache()");
        String bearerAccessTkHeader = doHttpAuthHeader(oauthToken);

        if (bearerAccessTkHeader == null) {
            Timber.d("checkBearerTokenInCache(), bearerAccessTkHeader == null");
            ErrorBean errorBean = new ErrorBean(TOKEN_NULL.getHttpMessage(), TOKEN_NULL.getHttpStatus());
            throw new UiException(errorBean);
        }
        Timber.d("checkBearerTokenInCache(), bearerAccessTkHeader == %s", bearerAccessTkHeader);
        return bearerAccessTkHeader;
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
}

