package com.didekindroid.api;

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
@SuppressWarnings("WeakerAccess")
public abstract class ControllerAbs implements ManagerIf.ControllerIf {

    private final CompositeDisposable subscriptions;

    protected ControllerAbs()
    {
        subscriptions = new CompositeDisposable();
    }

    public abstract ManagerIf.ViewerIf getViewer();

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
    public void processReactorError(Throwable e)
    {
        Timber.d("processViewerError()");
        UiException ui = getUiExceptionFromThrowable(e);
        getViewer().processControllerError(ui);
    }
}
