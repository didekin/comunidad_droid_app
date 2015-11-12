package com.didekin.common.exception;

/**
 * User: pedro
 * Date: 20/07/15
 * Time: 16:26
 */
public class ErrorBean {

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
