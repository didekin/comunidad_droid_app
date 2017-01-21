package com.didekindroid.usuario.delete;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 20/12/16
 * Time: 18:57
 */

final class DeleteMeReactor implements  DeleteMeReactorIf{

    static final DeleteMeReactorIf deleteReactor = new DeleteMeReactor();

    private DeleteMeReactor()
    {
    }

    //    .................................... OBSERVABLES .................................

    private static Single<Boolean> getDeleteMeSingle()
    {   // TODO: to test: devuelve true y devuelve false (no en BD).
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.deleteUser();
            }
        }).map(TKhandler.cleanTokenFunc);
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Override
    public boolean deleteMeInRemote(DeleteMeControllerIf controller){
        return controller.getSubscriptions().add(getDeleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new DeleteMeSingleObserver(controller)));
    }

    // ............................ SUBSCRIBERS ..................................

    private static class DeleteMeSingleObserver extends DisposableSingleObserver<Boolean> {

        private final DeleteMeControllerIf controller;

        DeleteMeSingleObserver(DeleteMeControllerIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Boolean isDeleted)
        { // TODO: test.
            controller.processBackDeleteMeRemote(isDeleted);

        }

        @Override
        public void onError(Throwable e)
        { // TODO: test.
            Timber.d("onError");
            controller.processErrorInReactor(e);
        }
    }
}
