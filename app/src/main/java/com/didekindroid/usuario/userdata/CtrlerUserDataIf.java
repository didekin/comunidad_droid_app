package com.didekindroid.usuario.userdata;


import com.didekindroid.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface CtrlerUserDataIf extends ControllerIf {

    boolean loadUserData(DisposableSingleObserver<Usuario> observer);

    boolean modifyUser(DisposableSingleObserver<Boolean> observer, Usuario oldUser, Usuario newUser);
}
