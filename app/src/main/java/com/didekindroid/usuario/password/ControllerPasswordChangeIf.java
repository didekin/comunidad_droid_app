package com.didekindroid.usuario.password;

import com.didekindroid.ManagerIf;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Completable;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 14:45
 */
interface ControllerPasswordChangeIf extends ManagerIf.ControllerIdentityIf {

    void changePasswordInRemote(Usuario usuario);

    void processBackChangedPswdRemote();

    // ................. REACTOR ....................

    interface ReactorPswdChangeIf {

        Completable isPasswordChanged(Usuario usuario);

        boolean passwordChange(ControllerPasswordChangeIf controller, Usuario password);
    }
}
