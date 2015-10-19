package com.didekin.retrofitcl;

import com.didekin.retrofitcl.OauthToken.AccessToken;

import static com.didekin.serviceone.security.SecurityConstant.BEARER_TOKEN_TYPE;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 20:16
 */
public enum OauthTokenHelper {

    HELPER,;

    public static final String BASIC_AND_SPACE = "Basic ";

    public String doBearerAccessTkHeader(AccessToken accessToken)
    {
        return BEARER_TOKEN_TYPE.substring(0, 1).toUpperCase() + BEARER_TOKEN_TYPE.substring(1)
                + " " + accessToken.getValue();
    }
}
