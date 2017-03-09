package com.didekindroid.security;


import android.content.Context;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 18:17
 */
@SuppressWarnings("WeakerAccess")
public interface IdentityCacher {

    void initIdentityCache(SpringOauthToken springOauthToken);

    void cleanIdentityCache();

    String doHttpAuthHeaderFromTkInCache() throws UiException;

    String doHttpAuthHeader(SpringOauthToken oauthToken) throws UiException;

    SpringOauthToken getAccessTokenInCache() throws UiException;

    Context getContext();

    AtomicReference<SpringOauthToken> getTokenCache();

    File getRefreshTokenFile();

    String getRefreshTokenValue();

    boolean isRegisteredUser();

    void updateIsRegistered(boolean isRegisteredUser);

    enum SharedPrefFiles {

        app_preferences_file,;

        public static final String IS_USER_REG = "TKhandler.isRegisteredUser";

        @Override
        public String toString()
        {
            return getClass().getCanonicalName().concat(".").concat(this.name());
        }
    }
}
