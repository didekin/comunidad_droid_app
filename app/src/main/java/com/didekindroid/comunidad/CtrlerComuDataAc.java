package com.didekindroid.comunidad;

import com.didekindroid.api.Controller;
import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.dao.UserComuObservable.comunidadModificada;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:25
 */

class CtrlerComuDataAc extends Controller {

    // .................................... INSTANCE METHODS .................................

    boolean modifyComunidadData(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
    {
        Timber.d("modifyComunidadData()");
        return subscriptions.add(
                comunidadModificada(comunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
