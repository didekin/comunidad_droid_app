package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.security.IdentityCacherIf;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.UiUtil.destroySubscriptions;


/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 10:43
 */
public class Controller implements ControllerIf {

    private final CompositeDisposable subscriptions;
    private final IdentityCacherIf tkCacher;

    public Controller()
    {
        this(secInitializer.get().getTkCacher());
    }

    public Controller(IdentityCacherIf identityCacher)
    {
        subscriptions = new CompositeDisposable();
        tkCacher = identityCacher;
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
        boolean isRegistered = tkCacher.isRegisteredUser();
        Timber.d("isRegisteredUser() = %b", isRegistered);
        return isRegistered;
    }

    @Override
    public void updateIsRegistered(boolean isRegisteredUser)
    {
        Timber.d("updateIsRegistered()");
        tkCacher.updateIsRegistered(isRegisteredUser);
    }

    @Override
    @NonNull
    public IdentityCacherIf getTkCacher()
    {
        Timber.d("getTkCacher()");
        return tkCacher;
    }
}
