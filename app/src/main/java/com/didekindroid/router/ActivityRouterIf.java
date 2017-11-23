package com.didekindroid.router;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 11/03/17
 * Time: 14:52
 */

interface ActivityRouterIf {

    Class<? extends Activity> nextActivityFromMn(int resourceId);

    Class<? extends Activity> nextActivity(Class<? extends Activity> previousActivity);
}
