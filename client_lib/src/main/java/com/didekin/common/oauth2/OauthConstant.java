package com.didekin.common.oauth2;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 11:02
 */
@SuppressWarnings("unused")
public final class OauthConstant {

    // Oauth2: Grant types.
    public static final String PASSWORD_GRANT = "password";
    public static final String REFRESH_TOKEN_GRANT = "refresh_token";
    // Oauth2: token scope.
    public static final String READ_WRITE_SCOPE = "readwrite";
    // Oauth2: token type.
    public static final String BEARER_TOKEN_TYPE = "bearer";

    /*Paths used by the authorization server*/
    public static final String TOKEN_PATH = "/oauth/token";
    /*Params used by the authorization server*/
    public static final String USER_PARAM = "username";
    public static final String PSWD_PARAM = "password";
    public static final String GCM_TOKEN_PARAM = "gcmtoken";
    public static final String REFRESH_TK_PARAM = "refresh_token";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String GRANT_TYPE_PARAM = "grant_type";

    /*Http basic authentication realm.*/
    public static final String USERS_RSRC_ID = "users_didekin";

    /*Paths used by the resource server.*/
    public static final String USER_PATH = "/users";
    public static final String USER_WRITE = USER_PATH + "/write";
    public static final String USER_READ = USER_PATH + "/read";
    public static final String COMUNIDAD_PATH = "/comunidad";
    public static final String COMUNIDAD_WRITE = COMUNIDAD_PATH + "/write";
    public static final String COMUNIDAD_READ = COMUNIDAD_PATH + "/read";
    public static final String INCID_PATH = "/incid";
    public static final String INCID_READ = INCID_PATH + "/read";
    public static final String INCID_WRITE = INCID_PATH + "/write";

    /*Common paths out of the control of both authorization and resource servers.*/
    public static final String ERROR = "/error";
    public static final String OPEN = "/open";
    public static final String OPEN_AREA = OPEN + "/**";

    /*Authorities. They are based on Rol enum class.*/
    public static final String ADMON_AUTH = Rol.ADMINISTRADOR.authority;
    public static final String USER_AUTH = Rol.PROPIETARIO.authority;

    private OauthConstant()
    {
    }
}


