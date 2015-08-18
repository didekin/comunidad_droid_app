package com.didekindroid.usuario.login.dominio;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 17:31
 */
public class AccessToken {

    // Constant values.
    public static final String READ_WRITE_SCOPE = "readwrite";
    public static final String BEARER_TOKEN_TYPE = "bearer";

    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;

    public AccessToken(String refresh_token)
    {
        token_type = BEARER_TOKEN_TYPE;
        scope = READ_WRITE_SCOPE;
        this.refresh_token = refresh_token;
    }

    public AccessToken(String access_token, int expires_in, String refresh_token, String scope, String token_type)
    {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
        this.token_type = token_type;
    }

    public String getAccess_token()
    {
        return access_token;
    }

    public int getExpires_in()
    {
        return expires_in;
    }

    public String getRefresh_token()
    {
        return refresh_token;
    }

    public String getScope()
    {
        return scope;
    }

    public String getToken_type()
    {
        return token_type;
    }

    public void setAccess_token(String access_token)
    {
        this.access_token = access_token;
    }
}

