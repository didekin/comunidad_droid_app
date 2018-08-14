package com.didekindroid.incidencia.core;

import com.didekindroid.incidencia.IncidenciaDao;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.CtrlerListIf;
import com.didekindroid.lib_one.incidencia.IncidenciaDataDbHelper;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:08
 */
public class CtrlerIncidenciaCore extends Controller implements CtrlerListIf<ImportanciaUser> {

    private final IncidenciaDao incidDaoRemote;

    public CtrlerIncidenciaCore()
    {
        super();
        incidDaoRemote = IncidenciaDao.incidenciaDao;
    }

    // .................................... INSTANCE METHODS .................................

    public boolean eraseIncidencia(DisposableSingleObserver<Integer> observer, Incidencia incidencia)
    {
        Timber.d("eraseIncidencia()");
        return getSubscriptions().add(
                incidDaoRemote.deleteIncidencia(incidencia.getIncidenciaId())
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
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<ImportanciaUser>> observer, long entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return getSubscriptions().add(
                incidDaoRemote.seeUserComusImportancia(entityId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean modifyIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia newIncidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return getSubscriptions().add(
                incidDaoRemote.modifyIncidImportancia(newIncidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regIncidImportancia(DisposableSingleObserver<Integer> observer, IncidImportancia incidImportancia)
    {
        Timber.d("regIncidImportancia()");
        return getSubscriptions().add(
                incidDaoRemote.regIncidImportancia(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regResolucion(DisposableSingleObserver<Integer> observer, Resolucion resolucion)
    {
        Timber.d("regResolucion()");
        return getSubscriptions().add(
                incidDaoRemote.regResolucion(resolucion)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean seeResolucion(DisposableMaybeObserver<Resolucion> observer, final long incidenciaId)
    {
        Timber.d("seeResolucionInBundle()");
        return getSubscriptions().add(
                incidDaoRemote.seeResolucionRaw(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
