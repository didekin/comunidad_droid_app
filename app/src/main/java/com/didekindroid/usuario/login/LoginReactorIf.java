package com.didekindroid.usuario.login;

import com.didekin.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 19/01/17
 * Time: 19:59
 */
interface LoginReactorIf {
    boolean validateLogin(LoginControllerIf controller, Usuario usuario);
    boolean sendPasswordToUser(LoginControllerIf controller, Usuario usuario);
}
