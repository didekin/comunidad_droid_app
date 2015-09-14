package com.didekindroid.usuario.webservices;

import com.didekin.retrofitcl.Oauth2EndPoints.BodyText;
import com.didekin.security.OauthToken.AccessToken;

/**
 * User: pedro@didekin
 * Date: 07/09/15
 * Time: 11:05
 */

/**
 * Convenience methods for those in Oath2EndPoints.
 */
public interface Oauth2ServiceIf {

    AccessToken getPasswordUserToken(String userName, String password);

    AccessToken getRefreshUserToken(String refreshTokenKey);
}
