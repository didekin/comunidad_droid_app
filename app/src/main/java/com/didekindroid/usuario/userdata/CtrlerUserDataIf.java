package com.didekindroid.usuario.userdata;


import com.didekindroid.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface CtrlerUserDataIf extends ControllerIf {

    boolean loadUserData();

    boolean modifyUser(Usuario oldUser, Usuario newUser);

    void onSuccessUserDataLoaded(Usuario usuario);

    void onCompleteUserModified();
}
