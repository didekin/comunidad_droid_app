package com.didekindroid.incidencia.core;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ControllerListIf;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
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
        return getSubscriptions().add(
                incidenciaDeleted(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public String getAmbitoIncidDesc(short ambitoId)
    {
        Timber.d("getAmbitoIncidDesc()");
        IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(getTkCacher().getContext());
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
        return getSubscriptions().add(
                singleObservable
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean modifyIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia newIncidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return getSubscriptions().add(
                incidImportanciaModified(newIncidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean registerIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia incidImportancia)
    {
        Timber.d("registerIncidImportancia()");
        return getSubscriptions().add(
                incidImportanciaRegistered(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, final long incidenciaId)
    {
        Timber.d("seeResolucion()");
        return getSubscriptions().add(
                resolucion(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
