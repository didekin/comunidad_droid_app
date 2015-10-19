package com.didekin.retrofitcl;

import java.lang.String;import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 02/09/15
 * Time: 14:25
 */
public class OauthToken {

    private final String value;
    private final Timestamp expiration;

    public OauthToken(String value, Timestamp expiration)
    {
        this.expiration = expiration;
        this.value = value;
    }

    public Timestamp getExpiration()
    {
        return expiration;
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
            this.scope = scope;
        }

        public OauthToken getRefreshToken()
        {
            return refreshToken;
        }

        public String[] getScope()
        {
            return scope;
        }

        public String getTokenType()
        {
            return tokenType;
        }
    }
}
