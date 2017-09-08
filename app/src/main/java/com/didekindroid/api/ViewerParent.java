package com.didekindroid.api;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 15:25
 */

public class ViewerParent<T extends View, C extends ControllerIf> extends Viewer<T, C> implements
        ParentViewerInjectedIf<T, C> {

    private final Map<Class<? extends ViewerIf>, ViewerIf> childViewers;

    public ViewerParent(T view, AppCompatActivity activity)
    {
        super(view, activity, null);
        childViewers = new HashMap<>(1);
    }

    @Override
    public void setChildViewer(@NonNull ViewerIf childViewer)
    {
        Timber.d("setChildViewer()");
        childViewers.put(childViewer.getClass(), childViewer);
    }

    @Override
    public <H extends ViewerIf> H getChildViewer(Class<H> viewerChildClass)
    {
        Timber.d("getChildViewer()");
        return viewerChildClass.cast(childViewers.get(viewerChildClass));
    }
}
