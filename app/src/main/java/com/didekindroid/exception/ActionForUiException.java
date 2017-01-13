package com.didekindroid.exception;

import android.app.Activity;

import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;

/**
 * User: pedro@didekin
 * Date: 13/01/17
 * Time: 18:33
 */
class ActionForUiException implements ActionForUiExceptionIf {

    private final Class<? extends Activity> activityToGoClass;
    private final int toastResourceId;

    ActionForUiException(Class<? extends Activity> activityToGoClass, int toastResourceId)
    {
        this.activityToGoClass = activityToGoClass;
        this.toastResourceId = toastResourceId;
    }

    @Override
    public Class<? extends Activity> getActivityToGoClass()
    {
        return activityToGoClass;
    }

    @Override
    public int getToastResourceId()
    {
        return toastResourceId;
    }
}
