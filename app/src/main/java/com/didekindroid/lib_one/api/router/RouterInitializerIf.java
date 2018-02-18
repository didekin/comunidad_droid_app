package com.didekindroid.lib_one.api.router;

import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:39
 */

public interface RouterInitializerIf {

    UiExceptionRouterIf getExceptionRouter();

    MnRouterIf getMnRouter();

    ContextualRouterIf getContextRouter();
}
