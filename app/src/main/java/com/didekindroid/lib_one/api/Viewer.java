package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterInitializerIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 13:37
 */
public class Viewer<T extends View, C extends ControllerIf> implements ViewerIf<T, C> {

    protected final T view;
    protected final Activity activity;
    protected final ViewerIf parentViewer;
    private final UiExceptionRouterIf exceptionRouter;
    private final ContextualRouterIf contextualRouter;
    protected C controller;

    protected Viewer(T view, Activity activity, ViewerIf parentViewer)
    {
        this(view, activity, parentViewer, routerInitializer.get());
    }

    protected Viewer(T view, Activity activity, ViewerIf parentViewer, RouterInitializerIf routerInitializerIn)
    {
        this.view = view;
        this.activity = activity;
        this.parentViewer = parentViewer;
        exceptionRouter = routerInitializerIn.getExceptionRouter();
        contextualRouter = routerInitializerIn.getContextRouter();
    }

    @Override
    public Activity getActivity()
    {
        Timber.d("getActivity()");
        return activity;
    }

    @Override
    public UiExceptionRouterIf getExceptionRouter()
    {
        Timber.d("getExceptionRouter()");
        return exceptionRouter;
    }

    protected ContextualRouterIf getContextualRouter()
    {
        Timber.d("getContextualRouter()");
        return contextualRouter;
    }

    @Override
    public void onErrorInObserver(Throwable error)
    {
        Timber.d("onErrorInObserver()");
        exceptionRouter.getActionFromMsg(getUiExceptionFromThrowable(error).getErrorHtppMsg()).initActivity(activity);
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
        Timber.d("getInjectedParentViewer()");
        return parentViewer;
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
    }
}
