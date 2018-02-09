package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.security.IdentityCacher;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.lib_one.util.UIutils.destroySubscriptions;

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
        boolean isRegistered = identityCacher.isRegisteredUser();
        Timber.d("isRegisteredUser() = %b", isRegistered);
        return isRegistered;
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");
        identityCacher.updateIsRegistered(isRegisteredUser);
    }

    @Override
    @NonNull
    public IdentityCacher getIdentityCacher()
    {
        Timber.d("getIdentityCacher()");
        return identityCacher;
    }
}
