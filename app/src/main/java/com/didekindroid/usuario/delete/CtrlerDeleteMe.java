package com.didekindroid.usuario.delete;

import com.didekindroid.api.Controller;

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
 * Date: 21/02/17
 * Time: 12:53
 */
@SuppressWarnings("WeakerAccess")
class CtrlerDeleteMe extends Controller implements CtrlerDeleteMeIf {

    // ................................. OBSERVABLES ...............................

    static Single<Boolean> getDeleteMeSingle()
    {
        Timber.d("getDeleteMeSingle()");

        return fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception
            {
                return usuarioDao.deleteUser();
            }
        }).map(cleanTokenAndUnregisterFunc);
    }

    // ................................. INSTANCE METHODS ...............................

    @Override
    public boolean deleteMeRemote(DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("deleteMeRemote()");
        return subscriptions.add(getDeleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer));
    }
}
