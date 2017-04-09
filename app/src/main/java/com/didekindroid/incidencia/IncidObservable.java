package com.didekindroid.incidencia;

import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 05/04/17
 * Time: 17:46
 */

public class IncidObservable {

    public static Single<Integer> incidenciaRegistered(final IncidImportancia incidImportancia)
    {
        Timber.d("incidenciaRegistered()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.regIncidImportancia(incidImportancia);
            }
        });
    }

    public static Single<Integer> incidImportanciaModified(final IncidImportancia incidImportancia){
        Timber.d("incidImportanciaModified()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                 return incidenciaDao.modifyIncidImportancia(incidImportancia);
            }
        });
    }

    public static Single<Integer> incidenciaDeleted(final Incidencia incidencia){
        Timber.d("incidImportanciaModified()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.deleteIncidencia(incidencia.getIncidenciaId());
            }
        });
    }

    public static Single<Resolucion> resolucion(final long incidenciaId)
    {
        return fromCallable(new Callable<Resolucion>() {
            @Override
            public Resolucion call() throws Exception
            {
                return incidenciaDao.seeResolucion(incidenciaId);
            }
        });
    }
}
