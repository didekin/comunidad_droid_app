package com.didekindroid;

import android.view.View;

import com.didekindroid.ManagerIf.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
public abstract class ViewerDumbImp<T extends View, B> implements ViewerIf<T, B> {

    protected final ManagerIf<B> manager;
    protected T viewInViewer;

    protected ViewerDumbImp(ManagerIf<B> manager)
    {
        this.manager = manager;
        viewInViewer = doViewInViewer(manager);
    }

    public abstract T doViewInViewer(ManagerIf<B> manager);

    @Override
    public ManagerIf<B> getManager()
    {
        return manager;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException e)
    {
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
