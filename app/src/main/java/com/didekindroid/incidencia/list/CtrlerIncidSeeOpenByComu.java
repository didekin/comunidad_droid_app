package com.didekindroid.incidencia.list;

import android.os.Bundle;

import com.didekindroid.incidencia.IncidenciaDao;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 17:56
 */
class CtrlerIncidSeeOpenByComu extends Controller implements CtrlerSelectListIf<IncidenciaUser> {

    private final IncidenciaDao incidDaoRemote;

    CtrlerIncidSeeOpenByComu()
    {
        super();
        incidDaoRemote = incidenciaDao;
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<IncidenciaUser>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId[0] > 0L, "Comunidad ID should be greater than 0");
        return getSubscriptions()
                .add(
                        incidDaoRemote.seeIncidsOpenByComu(entityId[0])
                                .subscribeOn(io())
                                .observeOn(mainThread())
                                .subscribeWith(observer)
                );
    }

    @Override
    public boolean selectItem(DisposableSingleObserver<Bundle> observer, IncidenciaUser item)
    {
        Timber.d("selectItem()");
        final Incidencia incidencia = item.getIncidencia();
        return getSubscriptions()
                .add(
                        incidDaoRemote.seeIncidImportancia(incidencia.getIncidenciaId())
                                .subscribeOn(io())
                                .observeOn(mainThread())
                                .subscribeWith(observer)
                );
    }
}
