package com.didekin.oauth2;

/**
 * User: pedro@didekin
 * Date: 27/04/15
 * Time: 13:50
 */
@SuppressWarnings("unused")
public enum OauthClient {

    CL_USER("user",""),
    CL_ADMON("admon",""),
    TEST1("test_one",""),
    ;

    private String id;
    private String secret;

    OauthClient(String id, String secret)
    {
        this.id = id;
        this.secret = secret;
    }

    public String getId()
    {
        return id;
    }

    public String getSecret()
    {
        return secret;
    }
}
