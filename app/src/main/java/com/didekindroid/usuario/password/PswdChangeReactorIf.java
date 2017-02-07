package com.didekindroid.usuario.password;


import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Completable;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 14:27
 */
interface PswdChangeReactorIf {
    Completable isPasswordChanged(Usuario usuario);
    boolean passwordChange(PasswordChangeControllerIf controller, Usuario password);
}
