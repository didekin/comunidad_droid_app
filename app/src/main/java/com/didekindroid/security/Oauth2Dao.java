package com.didekindroid.security;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.oauth2.SpringOauthToken;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 19:07
 */
interface Oauth2Dao {

    SpringOauthToken getPasswordUserToken(String userName, String password) throws UiException;

    SpringOauthToken getRefreshUserToken(String refreshTokenKey) throws UiException;
}
