package com.didekindroid.lib_one.security;


import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 18:17
 */
@SuppressWarnings("WeakerAccess")
public interface IdentityCacherIf {

    String identity_token_should_be_notnull = "Identity token should be not null";
    String is_notification_token_sent_server = "isGcmTokenSentToServer";

    String checkBearerTokenInCache() throws UiException;

    String checkBearerToken(SpringOauthToken oauthToken) throws UiException;

    void cleanIdentityCache();

    Context getContext();

    File getRefreshTokenFile();

    String getRefreshTokenValue();

    AtomicReference<SpringOauthToken> getTokenCache();

    void initIdentityCache(SpringOauthToken springOauthToken);

    boolean isRegisteredUser();

    void updateIsRegistered(boolean isRegisteredUser);

    boolean isGcmTokenSentServer();

    void updateIsGcmTokenSentServer(boolean isSentToServer);

    //  ======================================================================================
    //    ............................ ACTIONS AND FUNCTIONS .................................
    //  ======================================================================================

    Function<Boolean, Boolean> getCleanIdentityFunc();

    BiFunction<Boolean, SpringOauthToken, Boolean> getInitTokenAndRegisterFunc();

    Consumer<SpringOauthToken> getInitTokenUpdateRegisterAction();

    //  ======================================================================================
    //    ............................ Inner classes .................................
    //  ======================================================================================

    enum SharedPrefFiles {

        app_preferences_file,;

        public static final String is_user_registered = "IdentityCacher.isRegisteredUser";

        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(".").concat(name());
        }
    }
}
