package com.didekindroid;

import android.app.Activity;
import android.content.Intent;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 10:25
 */
public class ManagerDumbImp<B> implements ManagerIf<B> {

    final Activity activity;

    public ManagerDumbImp(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        return ui.processMe(activity, new Intent());
    }

    @Override
    public void replaceRootView(B initParamsForView)
    {
    }
}
