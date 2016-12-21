package com.didekinaar.usuario.userdata;

import com.didekin.usuario.Usuario;

import java.util.concurrent.Callable;

import rx.Single;

import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

class UserDataObservable {

    static Single<Usuario> getUserDataSingle()
    {
        return fromCallable(new UserDataCallable());
    }

    @SuppressWarnings("WeakerAccess")
    static class UserDataCallable implements Callable<Usuario> {
        @Override
        public Usuario call() throws Exception
        {
            return usuarioDaoRemote.getUserData();
        }
    }
}
