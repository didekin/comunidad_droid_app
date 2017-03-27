package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 13:37
 */
@SuppressWarnings("WeakerAccess")
public abstract class Viewer<T extends View, C extends ControllerIf> implements ViewerIf<T,C> {

    protected final T view;
    protected C controller;
    protected final Activity activity;
    protected final ViewerIf parentViewer;

    protected Viewer(T view, Activity activity, ViewerIf parentViewer)
    {
        this.view = view;
        this.activity = activity;
        this.parentViewer = parentViewer;
    }

    @Override
    public Activity getActivity()
    {
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
        return controller.clearSubscriptions();
    }

    @Override
    public T getViewInViewer()
    {
        return view;
    }

    @Override
    public C getController()
    {
        return controller;
    }

    @Override
    public void setController(C controller)
    {
        this.controller = controller;
    }

    /**
     *  It calls the parentViewer to replace the rootView.
     *  This method should be overwritten in viewers wich actually needs to replace the rootView for one
     *  of its child viewers.
     */
    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        if (parentViewer != null){
            parentViewer.replaceRootView(bundle);
        } else if (activity instanceof RootViewReplacerIf){
            ((RootViewReplacerIf) activity).replaceRootView(bundle);
        }
        else {
            throw new UnsupportedOperationException();
        }
    }

    @Nullable
    @Override
    public ViewerIf getParentViewer()
    {
        return parentViewer;
    }

    @Override
    public void saveState(Bundle savedState)
    {
    }
}
