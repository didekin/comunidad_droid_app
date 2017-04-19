package com.didekindroid.incidencia.list.close;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.didekindroid.api.Controller;
import com.didekindroid.api.CtrlerSelectableItemIf;
import com.didekindroid.api.ObserverMaybeList;
import com.didekindroid.api.ObserverSingleSelectedItem;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
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
        CtrlerSelectableItemIf<IncidenciaUser, Bundle> {

    CtrlerIncidSeeCloseByComu(ViewerIncidSeeClose incidViewer)
    {
        this(incidViewer, TKhandler);
    }

    private CtrlerIncidSeeCloseByComu(ViewerIncidSeeClose viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

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

    static Maybe<List<IncidenciaUser>> incidCloseList(final long comunidadId)
    {
        return Maybe.fromCallable(new Callable<List<IncidenciaUser>>() {
            @Override
            public List<IncidenciaUser> call() throws Exception
            {
                return incidenciaDao.seeIncidsClosedByComu(comunidadId);
            }
        });
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(Long... comunidadId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(comunidadId[0] > 0L, "Comunidad ID should be greater than 0");
        return subscriptions.add(
                incidCloseList(comunidadId[0])
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverMaybeList<>(this))
        );
    }

    @Override
    public void onSuccessLoadItemsInList(@NonNull List<IncidenciaUser> incidCloseList)
    {
        Timber.d("onSuccessLoadItemsInList();");
        ViewerIncidSeeClose.class.cast(viewer).onSuccessLoadItems(incidCloseList);
    }

    @Override
    public boolean selectItem(@NonNull IncidenciaUser incidenciaUser)
    {
        Timber.d("selectItem()");
        final Incidencia incidencia = incidenciaUser.getIncidencia();
        return subscriptions.add(
                bundleWithResolucion(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverSingleSelectedItem<>(this))
        );
    }

    @Override
    public void onSuccessSelectedItem(@NonNull Bundle bundle)
    {
        Timber.d("onSuccessSelectedItem()");
        ViewerIncidSeeClose.class.cast(viewer).replaceComponent(bundle);
    }
}