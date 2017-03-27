package com.didekindroid.api;

import android.view.View;

import com.didekindroid.exception.UiException;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.util.UIutils.destroySubscriptions;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
class Controller<T extends View> implements ControllerIf {

    protected final CompositeDisposable subscriptions;
    protected final ViewerIf<T, ? extends ControllerIf> viewer;

    Controller(ViewerIf<T, ? extends ControllerIf> viewer)
    {
        this.viewer = viewer;
        subscriptions = new CompositeDisposable();
    }

    @Override
    public CompositeDisposable getSubscriptions()
    {
        Timber.d("getSubscriptions()");
        return subscriptions;
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return destroySubscriptions(subscriptions);
    }

    @Override
    public ViewerIf<T, ? extends ControllerIf> getViewer()
    {
        return viewer;
    }

    @Override
    public void onErrorCtrl(Throwable e)
    {
        Timber.d("onErrorCtrl()");
        UiException ui = getUiExceptionFromThrowable(e);
        viewer.processControllerError(ui);
    }
}
