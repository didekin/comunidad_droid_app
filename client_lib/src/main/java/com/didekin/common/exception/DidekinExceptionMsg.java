package com.didekin.common.exception;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.bad_request_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.conflict_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.forbidden_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.not_found_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.postcondition_fail_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.precondition_fail_status;
import static com.didekin.common.exception.DidekinExceptionMsg.HttpStatus.unauthorized_status;

/**
 * User: pedro@didekin
 * Date: 07/11/15
 * Time: 13:58
 */
@SuppressWarnings("unused")
public enum DidekinExceptionMsg {

    BAD_REQUEST,
    COMUNIDAD_NOT_COMPARABLE,
    COMUNIDAD_DUPLICATE,
    COMUNIDAD_NOT_FOUND,
    COMUNIDAD_NOT_HASHABLE,
    COMUNIDAD_WRONG_INIT,
    NOT_FOUND,
    ROLES_NOT_FOUND,
    SUFIJO_NUM_IN_COMUNIDAD_NULL,
    TOKEN_NOT_DELETED,
    UNAUTHORIZED,
    UNAUTHORIZED_TX_TO_USER,
    USER_COMU_NOT_FOUND,
    USER_DATA_NOT_MODIFIED,
    USER_NOT_COMPARABLE,
    USER_NAME_NOT_FOUND,
    USER_NAME_DUPLICATE,
    USER_NOT_HASHABLE,
    USER_WRONG_INIT,
    USERCOMU_WRONG_INIT,;

    public enum HttpStatus {

        bad_request_status("Bad Request", 400),
        conflict_status(null, 409),
        forbidden_status(null, 403),
        not_found_status("Not Found", 404),
        precondition_fail_status(null, 412),
        postcondition_fail_status(null, 417),
        unauthorized_status("Unauthorized", 401),;

        final String statusMsg;
        final int statusCode;

        HttpStatus(String codeString, int codeNumber)
        {
            statusMsg = codeString;
            statusCode = codeNumber;
        }
    }

    static final EnumMap<DidekinExceptionMsg, HttpStatus> messageToStatusCode =
            new EnumMap<>(DidekinExceptionMsg.class);

    static final Set<String> loginHttpMessages = new HashSet<>();

    static {
        messageToStatusCode.put(BAD_REQUEST, bad_request_status);
        messageToStatusCode.put(COMUNIDAD_NOT_COMPARABLE, precondition_fail_status);
        messageToStatusCode.put(COMUNIDAD_DUPLICATE, conflict_status);
        messageToStatusCode.put(COMUNIDAD_NOT_FOUND, not_found_status);
        messageToStatusCode.put(COMUNIDAD_NOT_HASHABLE, precondition_fail_status);
        messageToStatusCode.put(COMUNIDAD_WRONG_INIT, precondition_fail_status);
        messageToStatusCode.put(NOT_FOUND, not_found_status);
        messageToStatusCode.put(ROLES_NOT_FOUND, not_found_status);
        messageToStatusCode.put(SUFIJO_NUM_IN_COMUNIDAD_NULL, precondition_fail_status);
        messageToStatusCode.put(TOKEN_NOT_DELETED, postcondition_fail_status);
        messageToStatusCode.put(UNAUTHORIZED, unauthorized_status);
        messageToStatusCode.put(UNAUTHORIZED_TX_TO_USER, forbidden_status);
        messageToStatusCode.put(USER_COMU_NOT_FOUND, not_found_status);
        messageToStatusCode.put(USER_DATA_NOT_MODIFIED,postcondition_fail_status);
        messageToStatusCode.put(USER_NOT_COMPARABLE, precondition_fail_status);
        messageToStatusCode.put(USER_NAME_NOT_FOUND, not_found_status);
        messageToStatusCode.put(USER_NAME_DUPLICATE, conflict_status);
        messageToStatusCode.put(USER_NOT_HASHABLE, precondition_fail_status);
        messageToStatusCode.put(USER_WRONG_INIT, precondition_fail_status);
        messageToStatusCode.put(USERCOMU_WRONG_INIT, precondition_fail_status);

        // LogingMessages.
        loginHttpMessages.add(BAD_REQUEST.getHttpMessage());
        loginHttpMessages.add(ROLES_NOT_FOUND.getHttpMessage());
        loginHttpMessages.add(UNAUTHORIZED.getHttpMessage());
        loginHttpMessages.add(UNAUTHORIZED_TX_TO_USER.getHttpMessage());
        loginHttpMessages.add(USER_COMU_NOT_FOUND.getHttpMessage());
        loginHttpMessages.add(USER_NAME_NOT_FOUND.getHttpMessage());
    }


    public static int getStatusCode(DidekinExceptionMsg didekinExceptionMsg)
    {
        return messageToStatusCode.get(didekinExceptionMsg).statusCode;
    }

    public static String getStatusMessage(DidekinExceptionMsg exceptionMsg)
    {
        return messageToStatusCode.get(exceptionMsg).statusMsg;
    }

    public static boolean isMessageToLogin(String httpMessage)
    {
        return loginHttpMessages.contains(httpMessage);
    }

    public int getHttpStatus()
    {
        return getStatusCode(this);
    }

    public String getHttpMessage()
    {
        return getStatusMessage(this) == null ? name() : getStatusMessage(this);
    }
}
