package com.didekindroid.api.router;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 17/11/2017
 * Time: 14:13
 *
 * Mark interface to be used by ActivityRouter.
 */
@FunctionalInterface
public interface IntrospectRouterToAcIf {
    Class<? extends Activity> getActivityToGo();
}
