package com.didekin.oauth2;

import static com.didekin.oauth2.OauthConstant.BEARER_TOKEN_TYPE;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 20:16
 */
@SuppressWarnings("unused")
public enum OauthTokenHelper {

    HELPER,;

    public static final String BASIC_AND_SPACE = "Basic ";

    public String doBearerAccessTkHeader(SpringOauthToken springOauthToken)
    {
        return BEARER_TOKEN_TYPE.substring(0, 1).toUpperCase() + BEARER_TOKEN_TYPE.substring(1)
                + " " + springOauthToken.getValue();
    }
}
