package com.didekinservice.incidservice.gcm;

import com.didekin.common.exception.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 31/05/16
 * Time: 15:18
 */
public class GcmException extends Exception {

    private final ErrorBean errorBean;

    GcmException(ErrorBean errorBean)
    {
        this.errorBean = errorBean;
    }

    public ErrorBean getErrorBean()
    {
        return errorBean;
    }
}
