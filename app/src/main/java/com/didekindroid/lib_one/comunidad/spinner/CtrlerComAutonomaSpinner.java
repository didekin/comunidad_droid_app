package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import java.util.List;

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

@SuppressWarnings("WeakerAccess")
public class CtrlerComAutonomaSpinner extends CtrlerSelectList<ComunidadAutonoma> {

    // .................................... OBSERVABLE .......................................

    Single<List<ComunidadAutonoma>> comunidadesAutonomasList()
    {
        Timber.d("comunidadesAutonomasList()");
        return Single.fromCallable(() -> {
            ComunidadDbHelper dbHelper = new ComunidadDbHelper(getTkCacher().getContext());
            List<ComunidadAutonoma> comunidades = dbHelper.getComunidadesAu();
            dbHelper.close();
            return comunidades;
        });
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<ComunidadAutonoma>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");

        return getSubscriptions().add(comunidadesAutonomasList()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }

    // .................................... INSTANCE METHODS .....................................


}
