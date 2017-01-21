package com.didekindroid.usuario.password;

import com.didekin.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 14:27
 */
interface PswdChangeReactorIf {
    boolean passwordChangeRemote(PasswordChangeControllerIf controller, Usuario usuario);
}
