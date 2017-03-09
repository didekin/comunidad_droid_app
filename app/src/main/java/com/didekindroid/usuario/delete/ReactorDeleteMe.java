package com.didekindroid.usuario.delete;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.cleanTokenAndUnregisterFunc;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 10:48
 */
final class ReactorDeleteMe implements ControllerDeleteMeIf.ReactorDeleteMeIf {

    static final ControllerDeleteMeIf.ReactorDeleteMeIf deleteReactor = new ReactorDeleteMe();

    private ReactorDeleteMe()
    {
    }

    // ................................. OBSERVABLES ...............................

    static Single<Boolean> getDeleteMeSingle()
    {
        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                Timber.d("fromCallable(), thread: %s", Thread.currentThread().getName());
                return usuarioDao.deleteUser();
            }
        }).map(cleanTokenAndUnregisterFunc);
    }

    // ............................ SUBSCRIPTIONS ..................................

    @Override
    public boolean deleteMeInRemote(ControllerDeleteMeIf controller)
    {
        return controller.getSubscriptions().add(getDeleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new DeleteMeSingleObserver(controller)));
    }

    // .............................. SUBSCRIBERS ..................................

    static class DeleteMeSingleObserver extends DisposableSingleObserver<Boolean> {

        private final ControllerDeleteMeIf controller;

        DeleteMeSingleObserver(ControllerDeleteMeIf controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(Boolean isDeleted)
        {
            Timber.d("onSuccess(), Thread: %s", Thread.currentThread().getName());
            controller.processBackDeleteMeRemote(isDeleted);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError, Thread: %s", Thread.currentThread().getName());
            controller.processReactorError(e);
        }
    }
}
