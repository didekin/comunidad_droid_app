package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.CtrlerSelectList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 10:08
 */

@SuppressWarnings("WeakerAccess")
public class CtrlerTipoViaSpinner extends CtrlerSelectList<TipoViaValueObj> {

    // .................................... OBSERVABLE .......................................

    Single<List<TipoViaValueObj>> tipoViaList()
    {
        Timber.d("tipoViaList()");

        return Single.fromCallable(new Callable<List<TipoViaValueObj>>() {
            @Override
            public List<TipoViaValueObj> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(getIdentityCacher().getContext());
                List<TipoViaValueObj> tiposVia = dbHelper.getTiposVia();
                dbHelper.close();
                return tiposVia;
            }
        });
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<TipoViaValueObj>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(tipoViaList()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }
}
