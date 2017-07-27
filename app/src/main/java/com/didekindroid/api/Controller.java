package com.didekindroid.api;

import android.support.annotation.NonNull;

import com.didekindroid.security.IdentityCacher;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.UIutils.destroySubscriptions;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
public class Controller implements ControllerIf {

    protected final CompositeDisposable subscriptions;
    protected final IdentityCacher identityCacher;

    public Controller()
    {
        this(TKhandler);
    }

    public Controller(IdentityCacher identityCacher)
    {
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

    @Override @NonNull
    public IdentityCacher getIdentityCacher()
    {
        Timber.d("getIdentityCacher()");
        return identityCacher;
    }
}
