package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.Controller;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadObservable.comunidad;
import static com.didekindroid.comunidad.ComunidadObservable.comunidadesFound;
import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadByUserAndComu;
import static com.didekindroid.usuariocomunidad.repository.UserComuObservable.comunidadModificada;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 15:42
 */

class CtrlerComunidad extends Controller {

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

    public boolean getUserComu(DisposableMaybeObserver<UsuarioComunidad> observer, Comunidad comunidad)
    {
        Timber.d("getUserComu()");
        return subscriptions.add(
                comunidadByUserAndComu(comunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean loadComunidadesFound(DisposableSingleObserver<List<Comunidad>> observer, Comunidad comunidadToSearch)
    {
        Timber.d("loadComunidadesFound()");
        return subscriptions.add(
                comunidadesFound(comunidadToSearch)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
