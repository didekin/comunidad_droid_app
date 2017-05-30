package com.didekindroid.incidencia.list.close;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekindroid.api.CtrlerSelectListIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.util.UIutils.assertTrue;
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
        return Single.fromCallable(new Callable<Resolucion>() {
            @Override
            public Resolucion call() throws Exception
            {
                return incidenciaDao.seeResolucion(incidencia.getIncidenciaId());
            }
        }).map(new Function<Resolucion, Bundle>() {
            @Override
            public Bundle apply(@NonNull Resolucion resolucion) throws Exception
            {
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, true);
                bundle.putSerializable(INCIDENCIA_OBJECT.key, incidencia);
                bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
                return bundle;
            }
        });
    }

    /**
     * It returns a Single to simplify the API, although the list can be empty.
     */
    static Single<List<IncidenciaUser>> incidCloseList(final long comunidadId)
    {
        return Single.fromCallable(new Callable<List<IncidenciaUser>>() {
            @Override
            public List<IncidenciaUser> call() throws Exception
            {
                return incidenciaDao.seeIncidsClosedByComu(comunidadId);
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
