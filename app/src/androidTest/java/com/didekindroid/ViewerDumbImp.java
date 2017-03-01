package com.didekindroid;

import android.app.Activity;
import android.view.View;

import com.didekindroid.ManagerIf.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
public abstract class ViewerDumbImp<T extends View, B> implements ViewerIf<T, B> {

    protected final Activity activity;
    protected T viewInViewer;

    protected ViewerDumbImp(Activity activity)
    {
        this.activity = activity;
        viewInViewer = doViewInViewer(activity);
    }

    @SuppressWarnings("unchecked")
    public T doViewInViewer(Activity activity)
    {
        return (T) new View(activity);
    }

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException e)
    {
        Timber.d("====================== processControllerError() ====================");
        return null;
    }

    @Override
    public int clearControllerSubscriptions()
    {
        return 99;
    }

    @Override
    public T getViewInViewer()
    {
        return viewInViewer;
    }
}
