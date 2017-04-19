package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.didekindroid.api.Controller;
import com.didekindroid.api.CtrlerSelectableItemIf;
import com.didekindroid.api.ObserverMaybeList;
import com.didekindroid.api.ObserverSingleSelectedItem;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
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
        CtrlerSelectableItemIf<IncidenciaUser, Bundle> {

    final ArrayAdapter<IncidenciaUser> adapter;

    CtrlerIncidSeeOpenByComu(ViewerIncidSeeOpen incidViewer)
    {
        this(incidViewer, TKhandler);
    }

    CtrlerIncidSeeOpenByComu(ViewerIncidSeeOpen viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
        adapter = new AdapterIncidSeeOpenByComu(viewer.getActivity());
    }

    /* .................................... OBSERVABLES .................................*/

    static Maybe<List<IncidenciaUser>> incidOpenList(final long comunidadId)
    {
        return Maybe.fromCallable(new Callable<List<IncidenciaUser>>() {
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
    public boolean loadItemsByEntitiyId(Long... comunidadId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(comunidadId[0] > 0L, "Comunidad ID should be greater than 0");
        return subscriptions.add(
                incidOpenList(comunidadId[0])
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverMaybeList<>(this))
        );
    }

    @Override
    public void onSuccessLoadItemsInList(List<IncidenciaUser> incidOpenList)
    {
        Timber.d("onSuccessLoadItemsInList()");
        ViewerIncidSeeOpen.class.cast(viewer).onSuccessLoadItems(incidOpenList);
    }

    @Override
    public boolean selectItem(@NonNull IncidenciaUser incidenciaUser)
    {
        Timber.d("selectItem()");
        final Incidencia incidencia = incidenciaUser.getIncidencia();
        return subscriptions.add(
                incidImportancia(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverSingleSelectedItem<>(this))
        );
    }

    @Override
    public void onSuccessSelectedItem(@NonNull Bundle bundle)
    {
        Timber.d("onSuccessSelectedItem()");
        ViewerIncidSeeOpen.class.cast(viewer).replaceComponent(bundle);
    }
}