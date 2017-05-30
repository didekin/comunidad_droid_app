package com.didekindroid.api;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:27
 */
@SuppressWarnings("WeakerAccess")
public class ObserverCacheCleaner extends DisposableCompletableObserver {

    private final ControllerIf controller;

    public ObserverCacheCleaner(ControllerIf controller)
    {
        this.controller = controller;
    }

    @Override
    public void onComplete()
    {
        Timber.d("onComplete(), Thread for subscriber: %s", Thread.currentThread().getName());
        dispose();
    }

    /**
     * If there is an error, the cache for auth tokens is cleared. The user will be forced to
     * login in the next access to a restricted activity.
     */
    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorObserver(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.getIdentityCacher().cleanIdentityCache();
        dispose();
    }
}
