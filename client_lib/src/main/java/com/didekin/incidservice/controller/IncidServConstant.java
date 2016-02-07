package com.didekin.incidservice.controller;

import static com.didekin.common.oauth2.OauthConstant.INCID_READ;
import static com.didekin.common.oauth2.OauthConstant.INCID_WRITE;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 17:06
 */
public final class IncidServConstant {

    private IncidServConstant()
    {
    }

    // Paths.
    public static final String INCIDENCIA_USER_DELETE = INCID_WRITE + "/del_incidenciaUser";
    public static final String SEE_INCID_BY_COMU = INCID_READ;
    public static final String SEE_INCID_CLOSED_BY_COMU = INCID_READ + "/closed";
    public static final String GET_INCID_USER_BY_INCID = INCID_READ + "/incidenciaUser";
    // Modify incidencia and user data incidenciaUser.
    public static final String MOD_INCID_USER = INCID_WRITE + "/mod_incidenciaUser";
    // Modify user data in incidenciaUser.
    public static final String MOD_USER = INCID_WRITE + "/mod_user";
    public static final String REG_INCID_COMMENT = INCID_WRITE + "/comment";
    public static final String REG_INCID_USER = INCID_WRITE + "/reg_incidenciaUser";
    public static final String REG_USER_IN_INCID = INCID_WRITE + "/reg_userInIncidencia";
}
