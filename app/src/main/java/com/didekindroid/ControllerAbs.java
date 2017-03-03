package com.didekindroid;

import com.didekindroid.ManagerIf.ControllerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.UIutils.destroySubscriptions;
import static com.didekindroid.util.UIutils.getUiExceptionFromThrowable;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
public abstract class ControllerAbs implements ControllerIf {

    private final CompositeDisposable subscriptions;
    private final IdentityCacher identityCacher;

    protected ControllerAbs()
    {
        this(TKhandler);
    }

    protected ControllerAbs(IdentityCacher identityCacher)
    {
        subscriptions = new CompositeDisposable();
        this.identityCacher = identityCacher;
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

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("isRegisteredUser()");
        return identityCacher.isRegisteredUser();
    }

    IdentityCacher getIdentityCacher()
    {
        return identityCacher;
    }
}
