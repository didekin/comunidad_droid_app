package com.didekin.oauth2;

import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 02/09/15
 * Time: 13:12
 *
 * Json container of the access and refresh tokens as received form the Spring Oauth2 implementation.
 * Example:
 * {"value":"3b052dcd-319e-42b2-ae24-0e483064c257",
 * "expiration":"Sep 22, 2016 10:49:03 PM",
 * "tokenType":"bearer",
 * "refreshToken":{"expiration":"Nov 21, 2016 9:49:03 AM","value":"37b5e17d-42f9-4a97-89c1-00c64a33253d"},
 * "scope":["readwrite"],"additionalInformation":{}}
 */
public final class SpringOauthToken {

    private final String value;
    private final Timestamp expiration;
    private final OauthToken refreshToken;
    private final String tokenType;
    private final String[] scope;

    public SpringOauthToken(String refreshTokenValue)
    {
        if (refreshTokenValue == null || refreshTokenValue.trim().isEmpty()){
            throw new IllegalStateException("RefreshToken null or empty");
        }
        this.refreshToken = new OauthToken(refreshTokenValue, null);
        value = null;
        expiration = null;
        tokenType = null;
        scope = null;
    }

    public SpringOauthToken(String value, Timestamp expiration, String tokenType, OauthToken refreshToken, String[] scope)
    {
        if (refreshToken.getValue() == null || refreshToken.getValue().trim().isEmpty()){
            throw new IllegalStateException("RefreshToken null or empty");
        }
        this.value = value;
        this.expiration = expiration;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.scope = scope != null ? scope.clone() : null;
    }

    public String getValue()
    {
        return value;
    }

    public Timestamp getExpiration()
    {
        return expiration != null ? new Timestamp(expiration.getTime()) : null;
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

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpringOauthToken that = (SpringOauthToken) o;

        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;
        if (expiration != null ? !expiration.equals(that.expiration) : that.expiration != null)
            return false;
        return refreshToken.equals(that.refreshToken);

    }

    @Override
    public int hashCode()
    {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
        result = 31 * result + refreshToken.hashCode();
        return result;
    }

    //    ================================ BUILDER =================================

    /**
     *  Oauth token encapsulates the String and the time validity of an Oauth2 Spring refresh token.
     *  Refresh tokens are not reused.
     */
    public final static class OauthToken {

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
    }
}
