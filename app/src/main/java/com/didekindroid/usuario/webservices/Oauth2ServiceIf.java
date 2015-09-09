package com.didekindroid.usuario.webservices;

import com.didekin.security.OauthToken;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:05
 */
public interface Oauth2ServiceIf {
    OauthToken.AccessToken getPasswordUserToken(String userName, String password);

    OauthToken.AccessToken getRefreshUserToken(String refreshTokenKey);
}
