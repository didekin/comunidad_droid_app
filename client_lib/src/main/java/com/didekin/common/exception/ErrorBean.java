package com.didekin.common.exception;

import static com.didekin.common.exception.DidekinExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro
 * Date: 20/07/15
 * Time: 16:26
 */
@SuppressWarnings("unused")
public class ErrorBean {

    public static final ErrorBean GENERIC_ERROR = new ErrorBean(GENERIC_INTERNAL_ERROR.getHttpMessage(), GENERIC_INTERNAL_ERROR.getHttpStatus());
    private final String message;
    private final int httpStatus;

    public ErrorBean(String message, int httpStatus)
    {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus()
    {
        return httpStatus;
    }

    public String getMessage()
    {
        return message;
    }
}
