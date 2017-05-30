package com.didekindroid.incidencia.list.open;

import android.os.Bundle;

import com.didekindroid.api.Controller;
import com.didekindroid.api.CtrlerSelectListIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.util.UIutils.assertTrue;
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
        return Single.fromCallable(new Callable<List<IncidenciaUser>>() {
            @Override
            public List<IncidenciaUser> call() throws Exception
            {
                return incidenciaDao.seeIncidsOpenByComu(comunidadId);
            }
        });
    }

    static Single<Bundle> incidImportancia(final Incidencia incidencia)
    {
        return fromCallable(new Callable<IncidAndResolBundle>() {
            @Override
            public IncidAndResolBundle call() throws Exception
            {
                return incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId());
            }
        }).map(new Function<IncidAndResolBundle, Bundle>() {
            @Override
            public Bundle apply(IncidAndResolBundle incidResol) throws Exception
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidResol.getIncidImportancia());
                bundle.putBoolean(INCID_RESOLUCION_FLAG.key, incidResol.hasResolucion());
                return bundle;
            }
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
