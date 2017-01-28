package com.didekindroid.security;


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

    SpringOauthToken getAccessTokenInCache() throws UiException;

    AtomicReference<SpringOauthToken> getTokenCache();

    String doHttpAuthHeaderFromTkInCache() throws UiException;

    File getRefreshTokenFile();

    String getRefreshTokenValue();

    boolean isRegisteredUser();

    boolean isGcmTokenSentServer();

    void updateIsRegistered(boolean isRegisteredUser);

    void updateIsGcmTokenSentServer(boolean isSentToServer);
}
