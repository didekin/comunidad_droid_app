package com.didekindroid.usuario.userdata;

import com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 17:14
 */
interface UserDataReactorIf {
    boolean getUserInRemote(UserDataControllerIf controller);

    boolean modifyUserInRemote(UserDataControllerIf controllerIf, UserChangeToMake changeToMake, Usuario oldUser, Usuario newUser);

    void updateAndInitTokenCache(Usuario newUser);
}
