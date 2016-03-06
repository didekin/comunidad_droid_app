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

    public static final String DELETE_INCID = INCID_WRITE + "/delete";
    public static final String MOD_INCID_IMPORTANCIA = INCID_WRITE + "/modify";
    public static final String MOD_RESOLUCION = INCID_WRITE + "/resolucion/modify";
    public static final String REG_INCID_COMMENT = INCID_WRITE + "/comment/reg";
    public static final String REG_INCID_IMPORTANCIA = INCID_WRITE;
    public static final String REG_RESOLUCION = INCID_WRITE + "/resolucion/reg";
    public static final String SEE_INCID_IMPORTANCIA = INCID_READ;
    public static final String SEE_INCID_OPEN_BY_COMU = INCID_READ + "/see";
    public static final String SEE_INCID_CLOSED_BY_COMU = INCID_READ + "/see/closed";
    public static final String SEE_INCID_COMMENTS = INCID_READ + "/comment/see";
    public static final String SEE_RESOLUCION = INCID_READ + "/resolucion/see";
}
