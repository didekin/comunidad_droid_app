package com.didekindroid.router;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;

import java.util.Map;

import timber.log.Timber;

import static com.didekindroid.router.UiExceptionAction.exceptionMsgMap;

/**
 * User: pedro@didekin
 * Date: 10/02/2018
 * Time: 14:36
 */
public final class UiExceptionRouter implements UiExceptionRouterIf {

    public static final UiExceptionRouterIf uiException_router = new UiExceptionRouter(exceptionMsgMap);

    private final Map<String, UiExceptionAction> actionMap;

    private UiExceptionRouter(Map<String, UiExceptionAction> actionMap)
    {
       this.actionMap = actionMap;
    }

    @Override
    public UiExceptionAction getActionFromMsg(@NonNull String httpMsg)
    {
        Timber.d("getActionFromMsg()");
        return actionMap.get(httpMsg);
    }
}
