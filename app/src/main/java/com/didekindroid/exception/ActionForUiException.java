package com.didekindroid.exception;

import android.app.Activity;
import android.os.Bundle;

import com.didekindroid.api.router.ActionForUiExceptionIf;

/**
 * User: pedro@didekin
 * Date: 13/01/17
 * Time: 18:33
 */
public class ActionForUiException implements ActionForUiExceptionIf {

    private final Class<? extends Activity> activityToGo;
    private final int toastResourceId;
    private final Bundle bundle;

    ActionForUiException(int toastResourceId)
    {
        this(toastResourceId, null, null);
    }

    public ActionForUiException(int toastResourceId, Class<? extends Activity> activityToGo)
    {
        this(toastResourceId, activityToGo, null);
    }

    public ActionForUiException(int toastResourceId, Class<? extends Activity> activityToGo, Bundle bundle)
    {
        this.activityToGo = activityToGo;
        this.toastResourceId = toastResourceId;
        this.bundle = bundle;
    }

    @Override
    public Class<? extends Activity> getActivityToGo()
    {
        return activityToGo;
    }

    @Override
    public Bundle getExtrasForActivity()
    {
        return bundle != null ? bundle : new Bundle(0);
    }

    @Override
    public int getToastResourceId()
    {
        return toastResourceId;
    }
}
