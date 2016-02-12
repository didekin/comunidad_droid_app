package com.didekin.usuario.controller;

import static com.didekin.common.oauth2.OauthConstant.COMUNIDAD_READ;
import static com.didekin.common.oauth2.OauthConstant.OPEN;
import static com.didekin.common.oauth2.OauthConstant.USER_READ;
import static com.didekin.common.oauth2.OauthConstant.USER_WRITE;

/**
 * User: pedro@didekin
 * Date: 04/09/15
 * Time: 11:10
 */
@SuppressWarnings({"unused"})
public final class UsuarioServiceConstant {

    // Paths.
    public static final String ACCESS_TOKEN_DELETE = USER_WRITE + "/token/delete";
    public static final String COMUNIDAD_OLDEST_USER = COMUNIDAD_READ + "/oldest_user";
    public static final String COMUNIDAD_SEARCH = OPEN + "/comunidad_search";
    public static final String COMUS_BY_USER = USER_READ + "/comus_by_user";
    public static final String LOGIN = OPEN + "/login";
    public static final String PASSWORD_MODIFY = USER_WRITE + "/pswd";
    public static final String PASSWORD_SEND = OPEN + "/pswd_send";
    public static final String REG_COMU_AND_USER_AND_USERCOMU = OPEN + "/reg_comu_user_usercomu";
    public static final String REG_COMU_USERCOMU = USER_WRITE + "/reg_comu_usercomu";
    public static final String REG_USER_USERCOMU = OPEN + "/reg_user_usercomu";
    public static final String REG_USERCOMU = USER_WRITE + "/reg_usercomu";
    public static final String USERCOMU_DELETE = USER_WRITE + "/usercomus/delete";
    public static final String USERCOMU_MODIFY = USER_WRITE + "/usercomus";
    public static final String USERCOMU_READ = USER_READ + "/usercomus";
    public static final String USERCOMUS_BY_COMU = USER_READ + "/usercomus_by_comu";
    public static final String USERCOMUS_BY_USER = USER_READ + "/usercomus_by_user";
    public static final String USER_DELETE = USER_WRITE + "/delete";
    public static final String USER_READ_GCM_TOKEN = USER_WRITE + "/gcm_token_read";
    public static final String USER_WRITE_GCM_TOKEN = USER_WRITE + "/gcm_token";

    // Mime types.
    public static final String MIME_JSON = "application/json";
    public static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    // Code constants.
    public static final int IS_USER_DELETED = -1;

    private UsuarioServiceConstant()
    {
    }
}
