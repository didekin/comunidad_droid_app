package com.didekindroid.usuario.userdata;

import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 17:14
 */
interface UserDataReactorIf {

    boolean loadUserData(UserDataControllerIf controller);

    boolean modifyUser(UserDataControllerIf controller, Usuario oldUser, Usuario newUser);
}
