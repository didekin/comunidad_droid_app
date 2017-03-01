package com.didekindroid.usuario.login;

import com.didekindroid.ManagerIf.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.Single;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 10:30
 */
interface ControllerLoginIf extends ControllerIf {

    void validateLoginRemote(Usuario usuario);

    void processBackLoginRemote(Boolean isLoginOk);
    void processBackDialogPositiveClick(Boolean isSendPassword);
    void doDialogPositiveClick(Usuario usuario);

    // ................. REACTOR ....................

    interface ReactorLoginIf {
        Single<Boolean> loginPswdSendSingle(String email);
        boolean validateLogin(ControllerLoginIf controller, Usuario usuario);
        boolean sendPasswordToUser(ControllerLoginIf controller, Usuario usuario);
    }
}
