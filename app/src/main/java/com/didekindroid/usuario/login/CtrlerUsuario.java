package com.didekindroid.usuario.login;

import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekindroid.usuario.CtrlerUsuarioIf;
import com.didekindroid.usuario.dao.UsuarioDaoObservable;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
public class CtrlerUsuario extends Controller implements CtrlerUsuarioIf {

    //    ................................. INSTANCE METHODS .................................

    boolean validateLogin(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
    {
        Timber.i("validateLogin()");
        return subscriptions.add(
                UsuarioDaoObservable.loginUpdateTkCache(usuario)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean sendNewPassword(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull final Usuario usuario)
    {
        Timber.d("sendNewPassword()");
        Callable<Boolean> sendPswdCallable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDaoRemote.sendPassword(usuario.getUserName());
            }
        };
        return sendNewPassword(sendPswdCallable, observer);
    }

    /**
     * Test friendly variant.
     */
    boolean sendNewPassword(@NonNull Callable<Boolean> sendPswdCall,
                            @NonNull DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("sendNewPassword()");

        return subscriptions.add(
                UsuarioDaoObservable.loginPswdSendSingle(sendPswdCall)    // Borra token in cache.
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean changePasswordInRemote(DisposableCompletableObserver observer, final Usuario oldUser, final Usuario newUser)
    {
        Timber.d("changePasswordInRemote()");
        return subscriptions.add(
                UsuarioDaoObservable.passwordChangeWithPswdValidation(oldUser, newUser)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
