package com.didekindroid.incidencia.core;

import com.didekindroid.api.Controller;
import com.didekindroid.api.ControllerListIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaModified;
import static com.didekindroid.incidencia.IncidObservable.incidImportanciaRegistered;
import static com.didekindroid.incidencia.IncidObservable.incidenciaDeleted;
import static com.didekindroid.incidencia.IncidObservable.resolucion;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:08
 */
public class CtrlerIncidenciaCore extends Controller implements ControllerListIf {

    // .................................... INSTANCE METHODS .................................

    public boolean eraseIncidencia(DisposableSingleObserver<Integer> observer, Incidencia incidencia)
    {
        Timber.d("eraseIncidencia()");
        return subscriptions.add(
                incidenciaDeleted(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public String getAmbitoIncidDesc(short ambitoId)
    {
        Timber.d("getAmbitoIncidDesc()");
        IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(getIdentityCacher().getContext());
        String ambitoDesc = dbHelper.getAmbitoDescByPk(ambitoId);
        dbHelper.close();
        return ambitoDesc;
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

    public boolean modifyIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia newIncidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return subscriptions.add(
                incidImportanciaModified(newIncidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean registerIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia incidImportancia)
    {
        Timber.d("registerIncidImportancia()");
        return subscriptions.add(
                incidImportanciaRegistered(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, final long incidenciaId)
    {
        Timber.d("seeResolucion()");
        return subscriptions.add(
                resolucion(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}