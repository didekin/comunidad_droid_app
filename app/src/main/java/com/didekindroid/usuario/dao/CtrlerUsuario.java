package com.didekindroid.usuario.dao;

import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.dao.UsuarioDaoObservable.deleteMeSingle;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.loginPswdSendSingle;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.loginUpdateTkCache;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.passwordChangeWithPswdValidation;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.userData;
import static com.didekindroid.usuario.dao.UsuarioDaoObservable.userModifiedWithPswdValidation;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
public class CtrlerUsuario extends Controller implements CtrlerUsuarioIf {

    @Override
    public boolean changePassword(DisposableCompletableObserver observer, final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("changePassword()");
        return subscriptions.add(
                passwordChangeWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean deleteMe(DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("deleteMe()");
        return subscriptions.add(deleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer));
    }

    @Override
    public boolean loadUserData(DisposableSingleObserver<Usuario> observer)
    {
        Timber.d("loadUserData()");
        return subscriptions.add(
                userData()
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
                userModifiedWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer));
    }

    @Override
    public boolean sendNewPassword(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull final Usuario usuario)
    {
        Timber.d("sendNewPassword()");
        Callable<Boolean> sendPswdCallable = () -> usuarioDaoRemote.sendPassword(usuario.getUserName());
        return sendNewPassword(sendPswdCallable, observer);
    }

    /**
     * Test friendly variant.
     */
    @Override
    public boolean sendNewPassword(@NonNull Callable<Boolean> sendPswdCall,
                                   @NonNull DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("sendNewPassword()");

        return subscriptions.add(
                loginPswdSendSingle(sendPswdCall)    // Borra token in cache.
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean validateLogin(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
    {
        Timber.i("validateLogin()");
        return subscriptions.add(
                loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
