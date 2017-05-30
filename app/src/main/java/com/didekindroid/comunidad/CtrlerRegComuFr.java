package com.didekindroid.comunidad;

import com.didekindroid.api.Controller;
import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadObservable.comunidad;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 15:42
 */

class CtrlerRegComuFr extends Controller {

    // .................................... INSTANCE METHODS .................................

    boolean loadComunidadData(DisposableSingleObserver<Comunidad> observer, long comunidadId)
    {
        Timber.d("loadComunidadData()");
        return subscriptions.add(
                comunidad(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
