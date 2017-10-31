package com.didekindroid.usuario.delete;

import com.didekindroid.api.Controller;
import com.didekindroid.usuario.dao.UsuarioDaoObservable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 21/02/17
 * Time: 12:53
 */
@SuppressWarnings("WeakerAccess")
class CtrlerDeleteMe extends Controller implements CtrlerDeleteMeIf {

    // ................................. INSTANCE METHODS ...............................

    @Override
    public boolean deleteMeRemote(DisposableSingleObserver<Boolean> observer)
    {
        Timber.d("deleteMeRemote()");
        return subscriptions.add(UsuarioDaoObservable.deleteMeSingle()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer));
    }
}
