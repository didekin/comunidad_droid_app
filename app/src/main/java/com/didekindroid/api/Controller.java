package com.didekindroid.api;

import android.view.View;

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
public class Controller implements ControllerIf {

    protected final CompositeDisposable subscriptions;
    protected final ViewerIf<? extends View, ? extends ControllerIf> viewer;
    protected final IdentityCacher identityCacher;

    public Controller(ViewerIf<? extends View, ? extends ControllerIf> viewer)
    {
        this(viewer, TKhandler);
    }

    public Controller(ViewerIf<? extends View, ? extends ControllerIf> viewer, IdentityCacher identityCacher)
    {
        this.viewer = viewer;
        subscriptions = new CompositeDisposable();
        this.identityCacher = identityCacher;
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
    public ViewerIf<? extends View, ? extends ControllerIf> getViewer()
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

    @Override
    public boolean isRegisteredUser()
    {
        Timber.d("isRegisteredUser()");
        return identityCacher.isRegisteredUser();
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");
        identityCacher.updateIsRegistered(isRegisteredUser);
    }

    @Override
    public IdentityCacher getIdentityCacher()
    {
        return identityCacher;
    }
}
