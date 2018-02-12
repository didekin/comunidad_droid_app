package com.didekindroid.lib_one.api.exception;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.router.RouterActionIf;

/**
 * User: pedro@didekin
 * Date: 10/02/2018
 * Time: 14:01
 */
@FunctionalInterface
public interface UiExceptionRouterIf {
    RouterActionIf getActionFromMsg(@NonNull String httpMsg);
}
