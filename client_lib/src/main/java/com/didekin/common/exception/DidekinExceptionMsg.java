package com.didekin.common.exception;

/**
 * User: pedro@didekin
 * Date: 07/11/15
 * Time: 13:58
 */
public enum DidekinExceptionMsg {

    AVANCE_WRONG_INIT(null, 412),
    BAD_REQUEST("Bad Request", 400),
    COMUNIDAD_NOT_COMPARABLE(null, 412),
    COMUNIDAD_DUPLICATE(null, 409),
    COMUNIDAD_NOT_FOUND(null, 404),
    COMUNIDAD_NOT_HASHABLE(null, 412),
    COMUNIDAD_WRONG_INIT(null, 412),
    INCIDENCIA_COMMENT_WRONG_INIT(null, 412),
    INCIDENCIA_NOT_FOUND(null, 404),
    INCIDENCIA_NOT_REGISTERED(null, 409),
    INCIDENCIA_USER_WRONG_INIT(null, 412),
    INCIDENCIA_WRONG_INIT(null, 412),
    INCID_IMPORTANCIA_NOT_FOUND(null, 404),
    INCID_IMPORTANCIA_WRONG_INIT(null, 412),
    NOT_FOUND("Not Found", 404),
    RESOLUCION_DUPLICATE(null, 409), // There exists a resolucion for the same incidencia.
    RESOLUCION_NOT_FOUND(null, 404),
    RESOLUCION_WRONG_INIT(null, 412),
    ROLES_NOT_FOUND(null, 401),
    SUFIJO_NUM_IN_COMUNIDAD_NULL(null, 412),
    TOKEN_NOT_DELETED(null, 417),
    TOKEN_NULL(null, 400),
    UNAUTHORIZED("Unauthorized", 401),
    UNAUTHORIZED_TX_TO_USER(null, 401),
    USERCOMU_WRONG_INIT(null, 412),
    USER_COMU_NOT_FOUND(null, 404),
    USER_DATA_NOT_MODIFIED(null, 417),
    USER_NOT_COMPARABLE(null, 412),
    USER_NOT_EQUAL_ABLE(null, 412),
    USER_NAME_NOT_FOUND(null, 404),
    USER_NAME_DUPLICATE(null, 409),
    USER_NOT_HASHABLE(null, 412),
    USER_WRONG_INIT(null, 412),
    ;

    private final String httpMessage;
    private final int httpStatus;

    DidekinExceptionMsg(String httpMessage, int httpStatus)
    {
        this.httpMessage = httpMessage;
        this.httpStatus = httpStatus;
    }

    public String getHttpMessage()
    {
        return httpMessage == null ? name() : httpMessage;
    }

    public int getHttpStatus()
    {
        return httpStatus;
    }
}
