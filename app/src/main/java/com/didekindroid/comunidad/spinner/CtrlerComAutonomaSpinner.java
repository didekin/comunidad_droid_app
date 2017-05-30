package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.CtrlerSelectList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

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
 * Time: 18:51
 */

class CtrlerComAutonomaSpinner extends CtrlerSelectList<ComunidadAutonoma> {

    // .................................... OBSERVABLE .......................................

    Single<List<ComunidadAutonoma>> comunidadesAutonomasList()
    {
        Timber.d("comunidadesAutonomasList()");
        return Single.fromCallable(new Callable<List<ComunidadAutonoma>>() {
            @Override
            public List<ComunidadAutonoma> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(getIdentityCacher().getContext());
                List<ComunidadAutonoma> comunidades = dbHelper.getComunidadesAu();
                dbHelper.close();
                return comunidades;
            }
        });
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<ComunidadAutonoma>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");

        return subscriptions.add(comunidadesAutonomasList()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }

    // .................................... INSTANCE METHODS .....................................


}
