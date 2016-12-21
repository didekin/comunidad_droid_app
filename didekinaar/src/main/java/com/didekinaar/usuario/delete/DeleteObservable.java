package com.didekinaar.usuario.delete;

import java.util.concurrent.Callable;

import rx.Single;

import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.usuario.UsuarioDaoRemote.usuarioDaoRemote;
import static com.didekinaar.utils.UIutils.updateIsRegistered;
import static rx.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

public class DeleteObservable {

    public static Single<Boolean> getDeleteMeSingle()
    {
        return fromCallable(new DeleteMeCallable());
    }

    @SuppressWarnings("WeakerAccess")
    static class DeleteMeCallable implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception
        {
            boolean isDeleted = usuarioDaoRemote.deleteUser();
            TKhandler.cleanTokenAndBackFile();
            updateIsRegistered(false, creator.get().getContext());
            return isDeleted;
        }
    }
}
