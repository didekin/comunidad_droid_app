package com.didekinaar.utils;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 13:07
 */

public interface ItemMenuIf {
    void doMenuItem(Activity activity, Class<? extends Activity> activityToGoClass);
}
