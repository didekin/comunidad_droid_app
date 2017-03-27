package com.didekindroid.usuario.userdata;


import com.didekindroid.api.CtrlerIdentityIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface CtrlerUserDataIf extends CtrlerIdentityIf {

    boolean loadUserData();

    boolean modifyUser(Usuario oldUser, Usuario newUser);

    void onSuccessUserDataLoaded(Usuario usuario);

    void onCompleteUserModified();
}
