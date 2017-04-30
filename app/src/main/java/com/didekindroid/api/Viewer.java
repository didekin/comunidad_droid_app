package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;

import java.io.Serializable;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 13:37
 */
@SuppressWarnings("WeakerAccess")
public class Viewer<T extends View, C extends ControllerIf> implements ViewerIf<T, C> {

    protected final T view;
    protected final Activity activity;
    protected final ViewerIf parentViewer;
    protected C controller;

    protected Viewer(T view, Activity activity, ViewerIf parentViewer)
    {
        this.view = view;
        this.activity = activity;
        this.parentViewer = parentViewer;
    }

    @Override
    public Activity getActivity()
    {
        Timber.d("getActivity()");
        return activity;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        return ui.processMe(activity, new Intent());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions();
    }

    @Override
    public T getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return view;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
    }

    @Override
    public C getController()
    {
        Timber.d("getController()");
        return controller;
    }

    @Override
    public void setController(C controller)
    {
        Timber.d("setController()");
        this.controller = controller;
    }

    @Nullable
    @Override
    public ViewerIf getParentViewer()
    {
        Timber.d("getParentViewer()");
        return parentViewer;
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
    }
}
