package com.didekindroid.incidencia.core.edit;

import com.didekindroid.api.Controller;
import com.didekindroid.incidencia.IncidObservable;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:24
 */

@SuppressWarnings("WeakerAccess")
class CtrlerIncidEditAc extends Controller {

    // .................................... INSTANCE METHODS .................................

    boolean seeResolucion(DisposableSingleObserver<Resolucion> observer, final long incidenciaId, final int resourceIdItemMn)
    {
        Timber.d("checkResolucion()");
        return subscriptions.add(
                IncidObservable.resolucion(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
