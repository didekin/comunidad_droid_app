package com.didekindroid.lib_one.api.router;

import android.support.annotation.NonNull;

/**
 * User: pedro@didekin
 * Date: 10/02/2018
 * Time: 14:01
 */
@FunctionalInterface
public interface UiExceptionRouterIf {
    RouterActionIf getActionFromMsg(@NonNull String httpMsg);
}
