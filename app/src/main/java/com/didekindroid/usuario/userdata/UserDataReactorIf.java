package com.didekindroid.usuario.userdata;

import com.didekin.usuario.Usuario;
import com.didekindroid.usuario.userdata.UserDataControllerIf.UserChangeToMake;

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
