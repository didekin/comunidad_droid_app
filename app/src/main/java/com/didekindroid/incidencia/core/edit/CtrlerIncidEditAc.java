package com.didekindroid.incidencia.core.edit;

import com.didekindroid.api.Controller;
import com.didekindroid.api.ControllerListIf;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidObservable.resolucion;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:24
 */

@SuppressWarnings("WeakerAccess")
public class CtrlerIncidEditAc extends Controller implements ControllerListIf {

    // .................................... INSTANCE METHODS .................................

    boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, final long incidenciaId)
    {
        Timber.d("seeResolucion()");
        return subscriptions.add(
                resolucion(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public <E extends Serializable> boolean loadItemsByEntitiyId(Single<List<E>> singleObservable,
                                                                 DisposableSingleObserver<List<E>> observer,
                                                                 long entityId)
    {
        Timber.d("loadItemsByEntityId()");
        return subscriptions.add(
                singleObservable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}