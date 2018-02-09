package com.didekindroid.incidencia.list;

import android.os.Bundle;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 17:56
 */
@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic", "TypeMayBeWeakened"})
class CtrlerIncidSeeOpenByComu extends Controller implements
        CtrlerSelectListIf<IncidenciaUser> {

    /* .................................... OBSERVABLES .................................*/

    static Single<List<IncidenciaUser>> incidOpenList(final long comunidadId)
    {
        return Single.fromCallable(() -> incidenciaDao.seeIncidsOpenByComu(comunidadId));
    }

    static Single<Bundle> incidImportancia(final Incidencia incidencia)
    {
        return fromCallable(() -> incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId()))
                .map(incidResol -> {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(INCID_RESOLUCION_BUNDLE.key, incidResol);
                    return bundle;
                });
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<IncidenciaUser>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId[0] > 0L, "Comunidad ID should be greater than 0");
        return subscriptions.add(
                incidOpenList(entityId[0])
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
        return subscriptions.add(
                incidImportancia(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
