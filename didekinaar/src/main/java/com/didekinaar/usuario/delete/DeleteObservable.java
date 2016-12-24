package com.didekinaar.usuario.delete;

import java.util.concurrent.Callable;

import rx.Single;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

class DeleteObservable {

    //    .................................... OBSERVABLES .................................

    static Single<Boolean> getDeleteMeSingle()
    {   // TODO: to test.
        return fromCallable(new DeleteMeCallable()).map(TKhandler.cleanTokenFunc);
    }

    //    .................................... CALLABLES .................................

    private static class DeleteMeCallable implements Callable<Boolean> {

        DeleteMeCallable(){}

        @Override
        public Boolean call() throws Exception
        {   // TODO: to test: devuelve true y devuelve false (no en BD).
            return usuarioDaoRemote.deleteUser();
        }
    }
}
