package com.didekindroid.lib_one.api.router;

import android.support.annotation.NonNull;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 16:05
 */

@FunctionalInterface
public interface ContextualRouterIf {
    RouterActionIf getActionFromContextNm(@NonNull ContextualNameIf contextualName);
}
