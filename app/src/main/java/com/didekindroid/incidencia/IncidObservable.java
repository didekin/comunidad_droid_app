package com.didekindroid.incidencia;

import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
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

    public static Single<Integer> incidImportanciaRegistered(final IncidImportancia incidImportancia)
    {
        Timber.d("incidImportanciaRegistered()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.regIncidImportancia(incidImportancia);
            }
        });
    }

    public static Single<Integer> incidImportanciaModified(final IncidImportancia incidImportancia)
    {
        Timber.d("incidImportanciaModified()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.modifyIncidImportancia(incidImportancia);
            }
        });
    }

    public static Single<Integer> incidenciaDeleted(final Incidencia incidencia)
    {
        Timber.d("incidImportanciaModified()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.deleteIncidencia(incidencia.getIncidenciaId());
            }
        });
    }

    public static Single<List<ImportanciaUser>> incidImportanciaByUsers(final long incidenciaId)
    {
        Timber.d("incidImportanciaByUsers()");
        return fromCallable(new Callable<List<ImportanciaUser>>() {
            @Override
            public List<ImportanciaUser> call() throws Exception
            {
                return incidenciaDao.seeUserComusImportancia(incidenciaId);
            }
        });
    }

    public static Maybe<Resolucion> resolucion(final long incidenciaId)
    {
        return Maybe.fromCallable(new Callable<Resolucion>() {
            @Override
            public Resolucion call() throws Exception
            {
                return incidenciaDao.seeResolucion(incidenciaId);
            }
        });
    }
}
