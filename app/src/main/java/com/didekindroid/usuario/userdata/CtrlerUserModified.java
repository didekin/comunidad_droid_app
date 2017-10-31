package com.didekindroid.usuario.userdata;

import com.didekindroid.api.Controller;
import com.didekindroid.usuario.dao.UsuarioDaoObservable;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 10:58
 */
@SuppressWarnings("WeakerAccess")
class CtrlerUserModified extends Controller implements CtrlerUserDataIf {

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadUserData(DisposableSingleObserver<Usuario> observer)
    {
        Timber.d("loadUserData()");
        return subscriptions.add(
                UsuarioDaoObservable.userDataLoaded()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean modifyUser(DisposableSingleObserver<Boolean> observer, Usuario oldUser, Usuario newUser)
    {
        Timber.d("modifyUser()");
        return subscriptions.add(
                UsuarioDaoObservable.userModifiedWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }
}

