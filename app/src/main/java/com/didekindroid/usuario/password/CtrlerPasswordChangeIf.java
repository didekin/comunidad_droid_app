package com.didekindroid.usuario.password;

import com.didekindroid.api.CtrlerIdentityIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 14:45
 */
interface CtrlerPasswordChangeIf extends CtrlerIdentityIf {

    boolean changePasswordInRemote(Usuario usuario);

    void onSuccessChangedPswd();
}
