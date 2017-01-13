package com.didekindroid.exception;

import android.app.Activity;
import android.content.Intent;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:18
 */

final class UiActionExceptionUtil {

    private UiActionExceptionUtil()
    {
    }

    static void finishActivity(Activity activity, Intent intent)
    {
        if (!activity.getClass().getCanonicalName().equals(intent.getComponent().getClassName())) {
            activity.finish();
        }
    }
}
