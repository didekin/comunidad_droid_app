package com.didekindroid.api;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 13:37
 */
@SuppressWarnings("WeakerAccess")
public class Viewer<T extends View, C extends ControllerIf> implements ViewerIf<T, C> {

    protected final T view;
    protected final AppCompatActivity activity;
    protected final ViewerIf parentViewer;
    protected C controller;

    protected Viewer(T view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        this.view = view;
        this.activity = activity;
        this.parentViewer = parentViewer;
    }

    @Override
    public AppCompatActivity getActivity()
    {
        Timber.d("getActivity()");
        return activity;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public ActionForUiExceptionIf onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        return getUiExceptionFromThrowable(error).processMe(activity, new Intent());
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
