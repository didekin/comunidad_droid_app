package com.didekinaar.exception;

import android.app.Activity;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:25
 */

public interface UiActionExceptionIf {

    Class<? extends Activity> getActivityToGoClass();
    int getToastResourceId();
}
