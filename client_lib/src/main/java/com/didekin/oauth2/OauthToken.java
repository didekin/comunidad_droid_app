package com.didekin.oauth2;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 02/09/15
 * Time: 14:25
 */
/**
 *  Oauth token encapsulates the String and the time validity of an Oauth2 Spring token.
 *  Access token encapsulates both the acces token and refresh token associated with it.
 *  Refresh tokens are not reused.
 * */
public class OauthToken {

    private final String value;
    private final Timestamp expiration;

    public OauthToken(String value, Timestamp expiration)
    {
        this.expiration = expiration != null ? new Timestamp(expiration.getTime()) : null;
        this.value = value;
    }

    @SuppressWarnings("unused")
    public Timestamp getExpiration()
    {
        return new Timestamp(expiration.getTime());
    }

    public String getValue()
    {
        return value;
    }

    /**
     * User: pedro@didekin
     * Date: 02/09/15
     * Time: 13:12
     */
    public static class AccessToken extends OauthToken {

        private final OauthToken refreshToken;
        private final String tokenType;
        private final String[] scope;

        public AccessToken(String value, Timestamp expiration,String tokenType, OauthToken refreshToken, String[] scope)
        {
            super(value,expiration);
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.scope = scope != null ? scope.clone() : null;
        }

        public OauthToken getRefreshToken()
        {
            return refreshToken;
        }

        public String[] getScope()
        {
            return scope.clone();
        }

        public String getTokenType()
        {
            return tokenType;
        }
    }
}