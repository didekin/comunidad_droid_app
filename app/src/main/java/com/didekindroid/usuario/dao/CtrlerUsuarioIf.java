package com.didekindroid.usuario.dao;

import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 11:22
 */

public interface CtrlerUsuarioIf extends ControllerIf{

    boolean changePassword(DisposableCompletableObserver observer, Usuario oldUser, Usuario newUser);

    boolean deleteMe(DisposableSingleObserver<Boolean> observer);

    boolean loadUserData(DisposableSingleObserver<Usuario> observer);

    boolean modifyUserName(DisposableSingleObserver<Boolean> observer, Usuario oldUser, Usuario newUser);

    boolean modifyUserAlias(DisposableSingleObserver<Boolean> observer, Usuario oldUser, Usuario newUser);

    @SuppressWarnings("UnusedReturnValue")
    boolean sendNewPassword(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario);

    boolean sendNewPassword(@NonNull Callable<Boolean> sendPswdCall,
                            @NonNull DisposableSingleObserver<Boolean> observer);

    boolean validateLogin(@NonNull DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario);
}
