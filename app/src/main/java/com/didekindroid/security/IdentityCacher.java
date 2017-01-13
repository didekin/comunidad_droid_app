package com.didekindroid.security;


import com.didekin.http.oauth2.SpringOauthToken;

/**
 * User: pedro@didekin
 * Date: 21/12/16
 * Time: 18:17
 */
interface IdentityCacher {

    void initIdentityCache(SpringOauthToken springOauthToken);

    void cleanIdentityCache();

    boolean isRegisteredUser();

    void updateIsRegistered(boolean isRegisteredUser);

    boolean isGcmTokenSentServer();

    void updateIsGcmTokenSentServer(boolean isSentToServer);
}
