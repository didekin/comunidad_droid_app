package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 17:15
 */
@SuppressWarnings({"TypeMayBeWeakened", "AnonymousInnerClassMayBeStatic", "WeakerAccess"})
public class CtrlerIncidSeeCloseByComu extends Controller implements
        CtrlerSelectListIf<IncidenciaUser> {

    // .................................... OBSERVABLES .................................

    static Single<Bundle> bundleWithResolucion(final Incidencia incidencia)
    {
        return Single.fromCallable(() -> incidenciaDao.seeResolucion(incidencia.getIncidenciaId()))
                .map(
                        resolucion -> {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
                            return bundle;
                        }
                );
    }

    /**
     * It returns a Single to simplify the API, although the list can be empty.
     */
    static Single<List<IncidenciaUser>> incidCloseList(final long comunidadId)
    {
        return Single.fromCallable(() -> incidenciaDao.seeIncidsClosedByComu(comunidadId));
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<IncidenciaUser>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId[0] > 0L, "Comunidad ID should be greater than 0");
        return subscriptions.add(
                incidCloseList(entityId[0])
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    @Override
    public boolean selectItem(DisposableSingleObserver<Bundle> observer, @NonNull IncidenciaUser incidenciaUser)
    {
        Timber.d("selectItem()");
        final Incidencia incidencia = incidenciaUser.getIncidencia();
        return subscriptions.add(
                bundleWithResolucion(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
