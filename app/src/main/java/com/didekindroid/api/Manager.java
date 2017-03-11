package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 16:11
 */

class Manager implements ManagerIf<Object> {

    private final Activity activity;

    Manager(Activity activity)
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
        Timber.d("processViewerError()");
        return ui.processMe(activity, new Intent());
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {
        Timber.d("replaceRootView()");
        Intent intent = new Intent(activity, ActivityNextMock.class);
        activity.startActivity(intent);
    }
}
