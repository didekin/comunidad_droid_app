package com.didekindroid.api;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:07
 */

interface ItemMenuIf {
    void doMenuItem(Activity activity, Class<? extends Activity> activityToGoClass);
}
