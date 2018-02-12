package com.didekindroid.lib_one.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.util.IoHelper.readStringFromFile;
import static com.didekindroid.lib_one.util.IoHelper.writeFileFromString;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekinlib.http.auth.AuthClient.doBearerAccessTkHeader;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class TokenIdentityCacher implements IdentityCacherIf {

    public static final IdentityCacherIf TKhandler = new TokenIdentityCacher(httpInitializer.get().getContext());

    //  ======================================================================================
    //    .................................... ACTIONS AND FUNCTIONS .................................
    //  ======================================================================================

    public static final Function<Boolean, Boolean> cleanTokenAndUnregisterFunc = isDeletedUser -> {
        if (isDeletedUser) {
            TKhandler.cleanIdentityCache();
            TKhandler.updateIsRegistered(false);
        }
        return isDeletedUser;
    };

    public static final Consumer<Boolean> cleanTkCacheConsumer = isUserModified -> {
        if (isUserModified) {
            TKhandler.cleanIdentityCache();
        }
    };

    public static final Consumer<Boolean> updateRegisterAction = TKhandler::updateIsRegistered;

    static final Consumer<SpringOauthToken> initTokenAction = TKhandler::initIdentityCache;

    static final BiFunction<Boolean, SpringOauthToken, Boolean> initTokenAndRegisterFunc = (isLoginValid, token) -> {
        boolean isUpdatedTokenData = isLoginValid && token != null;
        if (isUpdatedTokenData) {
            TKhandler.initIdentityCache(token);
            TKhandler.updateIsRegistered(true);
        }
        return isUpdatedTokenData;
    };

    static final Consumer<SpringOauthToken> initTokenUpdateRegisterAction = token -> {
        TKhandler.initIdentityCache(token);
        TKhandler.updateIsRegistered(true);
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
                new AtomicReference<>();
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
                (SharedPrefFiles.app_preferences_file.toString(), MODE_PRIVATE);
        boolean isRegistered = sharedPref.getBoolean(SharedPrefFiles.IS_USER_REG, false);
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

        SharedPreferences sharedPref = context.getSharedPreferences(SharedPrefFiles.app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SharedPrefFiles.IS_USER_REG, isRegisteredUser);
        if (!isRegisteredUser) {
            editor.putBoolean(is_notification_token_sent_server, false);
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
            return doBearerAccessTkHeader(oauthToken);
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
        Timber.d("checkBearerTokenInCache(SpringOauthToken oauthToken)");
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

