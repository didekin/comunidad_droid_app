package com.didekindroid.usuario.login;

import android.support.annotation.NonNull;

import com.didekindroid.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.Callable;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 10:30
 */
interface CtrlerLoginIf extends ControllerIf {

    boolean doDialogPositiveClick(DisposableSingleObserver<Boolean> observer, Usuario usuario);

    boolean validateLogin(DisposableSingleObserver<Boolean> observer, Usuario usuario);

    boolean doDialogPositiveClick(@NonNull Callable<Boolean> sendPswdCall,
                                  @NonNull DisposableSingleObserver<Boolean> observer,
                                  @NonNull Usuario usuario);
}
