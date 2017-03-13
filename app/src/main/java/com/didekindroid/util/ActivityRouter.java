package com.didekindroid.util;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 11/03/17
 * Time: 14:52
 */

public interface ActivityRouter {

    Class<? extends Activity> getNextActivity(Class<? extends Activity> previousActivity);
}
