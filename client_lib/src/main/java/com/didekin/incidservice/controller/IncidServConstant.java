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
    public static final String SEE_INCID_BY_COMU = INCID_READ;
    public static final String SEE_INCID_CLOSED_BY_COMU = INCID_READ + "/closed";
    public static final String REG_INCID_USERCOMU = INCID_WRITE + "/reg_incidusercomu";
}
