package com.didekinaar.exception;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:40
 */

public interface UiExceptionDealerIf {
    ActionForUiExceptionIf getActionForException(UiException uiException);
}
