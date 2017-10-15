package com.didekindroid.api;

import io.reactivex.observers.DisposableCompletableObserver;
import timber.log.Timber;

import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 25/05/17
 * Time: 09:27
 */
@SuppressWarnings("WeakerAccess")
public class ObserverCacheCleaner extends DisposableCompletableObserver {

    private final Viewer<?,?> viewer;

    public ObserverCacheCleaner(Viewer<?,?> viewer)
    {
        this.viewer = viewer;
        assertTrue(viewer.getController() != null, "Controller not null");
    }

    @Override
    public void onComplete()
    {
        Timber.d("onComplete()");
        dispose();
    }

    /**
     * If there is an error, the cache for auth tokens is cleared. The user will be forced to
     * login in the next access to a restricted activity.
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError, Thread for subscriber: %s", Thread.currentThread().getName());
        viewer.getController().getIdentityCacher().cleanIdentityCache();
        viewer.onErrorInObserver(e);
        dispose();
    }
}
