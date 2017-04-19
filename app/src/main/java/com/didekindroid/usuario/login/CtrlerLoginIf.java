package com.didekindroid.usuario.login;

import com.didekindroid.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 10:30
 */
interface CtrlerLoginIf extends ControllerIf {

    void onSuccessValidateLogin(Boolean isLoginOk);

    void onSuccessDialogPositiveClick(Boolean isSendPassword);

    boolean doDialogPositiveClick(Usuario usuario);

    boolean validateLogin(Usuario usuario);
}
