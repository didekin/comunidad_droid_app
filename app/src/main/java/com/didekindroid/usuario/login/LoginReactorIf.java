package com.didekindroid.usuario.login;


import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Single;

/**
 * User: pedro@didekin
 * Date: 19/01/17
 * Time: 19:59
 */
interface LoginReactorIf {
    Single<Boolean> loginPswdSendSingle(String email);

    boolean validateLogin(LoginControllerIf controller, Usuario usuario);
    boolean sendPasswordToUser(LoginControllerIf controller, Usuario usuario);
}
