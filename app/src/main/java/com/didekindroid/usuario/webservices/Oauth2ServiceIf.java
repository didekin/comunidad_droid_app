package com.didekindroid.usuario.webservices;

import com.didekin.retrofitcl.Oauth2EndPoints.BodyText;
import com.didekin.security.OauthToken.AccessToken;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:05
 */
public interface Oauth2ServiceIf {

    BodyText getHello();

    BodyText getHelloUserRead(String accessToken);

    AccessToken getPasswordUserToken(String userName, String password);

    AccessToken getRefreshUserToken(String refreshTokenKey);
}
