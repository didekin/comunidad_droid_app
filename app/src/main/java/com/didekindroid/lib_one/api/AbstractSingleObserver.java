package com.didekindroid.lib_one.api;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 16:58
 */
public abstract class AbstractSingleObserver<T> extends DisposableSingleObserver<T> {

    private final ViewerIf viewer;

    protected AbstractSingleObserver(ViewerIf viewer)
    {
        this.viewer = viewer;
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorObserver(), Thread for subscriber: %s", Thread.currentThread().getName());
        viewer.onErrorInObserver(e);
    }
}
