package com.didekinaar.security;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.exception.UiException;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 19:07
 */
interface Oauth2Dao {

    SpringOauthToken getPasswordUserToken(String userName, String password) throws UiException;
    SpringOauthToken getRefreshUserToken(String refreshTokenKey) throws UiException;
}
