package com.didekindroid.usuariocomunidad.spinner;

import com.didekindroid.api.CtrlerSelectList;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadesByUser;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:11
 */

@SuppressWarnings("WeakerAccess")
public class CtrlerComuSpinner extends CtrlerSelectList<Comunidad> {

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Comunidad>> observer, Long... entityId)
    {

        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(comunidadesByUser()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer));
    }
}
