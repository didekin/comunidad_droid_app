package com.didekindroid.common.error;

import java.net.HttpURLConnection;

import static java.net.HttpURLConnection.*;

/**
 * User: pedro@didekin
 * Date: 30/07/15
 * Time: 10:29
 */
public enum ServiceExceptionMessage {

    COMUNIDAD_NOT_FOUND(HTTP_NOT_FOUND, null),
    COMUNIDAD_NOT_TO_DELETE(HTTP_CONFLICT, null),
    USER_NOT_FOUND(HTTP_NOT_FOUND, null),
    BAD_REQUEST(HTTP_BAD_REQUEST, "Bad Request"),
    NOT_FOUND(HTTP_NOT_FOUND,"Not Found"),
    UNAUTHORIZED(HTTP_UNAUTHORIZED,"Unauthorized"),
    ;

    private int httpStatus;
    private String message;

    ServiceExceptionMessage(int httpStatus, String message)
    {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getHttpStatus()
    {
        return httpStatus;
    }

    public String getMessage()
    {
        return (message == null ? toString() : message);
    }
}
