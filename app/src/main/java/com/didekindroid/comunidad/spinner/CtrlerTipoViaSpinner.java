package com.didekindroid.comunidad.spinner;

import android.app.Activity;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SingleObserverSelectionList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 10:08
 */

class CtrlerTipoViaSpinner extends CtrlerSelectionList<TipoViaValueObj> {

    CtrlerTipoViaSpinner(ViewerTipoViaSpinner viewer)
    {
        super(viewer);
    }

    static CtrlerTipoViaSpinner newCtrlerTipoViaSpinner(ViewerTipoViaSpinner viewer)
    {
        Timber.d("newCtrlerTipoViaSpinner()");
        return new CtrlerTipoViaSpinner(viewer);
    }

    // .................................... OBSERVABLE .......................................

    static Single<List<TipoViaValueObj>> tipoViaList(final Activity activity)
    {
        Timber.d("tipoViaList()");

        return Single.fromCallable(new Callable<List<TipoViaValueObj>>() {
            @Override
            public List<TipoViaValueObj> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(activity);
                List<TipoViaValueObj> tiposVia = dbHelper.getTiposVia();
                dbHelper.close();
                return tiposVia;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(tipoViaList(viewer.getActivity())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new SingleObserverSelectionList<>(this))
        );
    }
}
