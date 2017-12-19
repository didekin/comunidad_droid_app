package com.didekindroid.api.router;

import com.didekindroid.exception.UiException;

/**
 * User: pedro@didekin
 * Date: 14/12/2017
 * Time: 12:38
 */
@FunctionalInterface
public interface UiExceptionRouterIf {
    ActionForUiExceptionIf getAction(UiException uiException);
}
