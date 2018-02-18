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
import static com.didekindroid.lib_one.security.IdentityCacherIf.SharedPrefFiles.app_preferences_file;
import static com.didekindroid.lib_one.security.IdentityCacherIf.SharedPrefFiles.is_user_registered;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.IoHelper.readStringFromFile;
import static com.didekindroid.lib_one.util.IoHelper.writeFileFromString;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekinlib.http.auth.AuthClient.doBearerAccessTkHeader;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;

/**
 * User: pedro@didekin
 * Date: 25/06/15
 * Time: 17:28
 */
public final class TokenIdentityCacher implements IdentityCacherIf {

    static final String refresh_token_filename = "tk_file";

    private final AtomicReference<SpringOauthToken> tokenCache;
    private final File refreshTokenFile;
    private final Context context;

    TokenIdentityCacher(Context contextIn)
    {
        this(new File(contextIn.getFilesDir(), refresh_token_filename), contextIn);
    }

    /**
     * It allows for a more friendly injection constructor.
     */
    private TokenIdentityCacher(File refreshTkFile, Context contextIn)
    {
        Timber.d("TokenIdentityCacher(File refreshTkFile, Context inContext)");
        refreshTokenFile = refreshTkFile;
        context = contextIn;
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
        boolean isRegistered = sharedPref.getBoolean(is_user_registered, false);
        Timber.d("isRegisteredUser() = %b", isRegistered);
        return isRegistered;
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");

        SharedPreferences sharedPref = context.getSharedPreferences(SharedPrefFiles.app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(is_user_registered, isRegisteredUser);
        if (!isRegisteredUser) {
            // We update new state about GCM token sent to server.
            editor.putBoolean(is_notification_token_sent_server, false);
        }
        editor.apply();
    }

    @Override
    public boolean isGcmTokenSentServer()
    {
        Timber.d("isGcmTokenSentServer()");
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        return sharedPref.getBoolean(IdentityCacherIf.is_notification_token_sent_server, false);
    }


    @Override
    public void updateIsGcmTokenSentServer(boolean isSentToServer)
    {
        Timber.d("updateIsGcmTokenSentServer(), isSentToServer = %b", isSentToServer);
        assertTrue(isRegisteredUser(), user_should_be_registered);
        SharedPreferences sharedPref = context.getSharedPreferences(app_preferences_file.toString(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IdentityCacherIf.is_notification_token_sent_server, isSentToServer);
        editor.apply();
    }

    //  ======================================================================================
    //    ............................ ACTIONS AND FUNCTIONS .................................
    //  ======================================================================================

    @Override
    public Function<Boolean, Boolean> getCleanIdentityFunc()
    {
        return isDeletedUser -> {
            if (isDeletedUser) {
                cleanIdentityCache();
                updateIsRegistered(false);
            }
            return isDeletedUser;
        };
    }

    @Override
    public BiFunction<Boolean, SpringOauthToken, Boolean> getInitTokenAndRegisterFunc()
    {
        return (isLoginValid, token) -> {
            boolean isUpdatedTokenData = isLoginValid && token != null;
            if (isUpdatedTokenData) {
                initIdentityCache(token);
                updateIsRegistered(true);
            }
            return isUpdatedTokenData;
        };
    }

    @Override
    public Consumer<SpringOauthToken> getInitTokenUpdateRegisterAction()
    {
        return token -> {
            initIdentityCache(token);
            updateIsRegistered(true);
        };
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

