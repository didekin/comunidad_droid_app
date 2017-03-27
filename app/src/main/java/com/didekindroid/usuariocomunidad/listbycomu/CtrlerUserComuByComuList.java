package com.didekindroid.usuariocomunidad.listbycomu;

import android.widget.ListView;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.api.CtrlerListIf;
import com.didekindroid.api.ObserverSingleList;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 10:59
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic"})
class CtrlerUserComuByComuList extends CtrlerIdentity<ListView> implements
        CtrlerListIf<UsuarioComunidad> {

    private final ViewerSeeUserComuByComu viewerList;

    CtrlerUserComuByComuList(ViewerSeeUserComuByComu viewer)
    {
        super(viewer);
        viewerList = viewer;
    }

    // .................................... OBSERVABLES .................................

    static Single<List<UsuarioComunidad>> listByEntityId(final long entityId)
    {
        Timber.d("listByEntityId()");
        return Single.fromCallable(new Callable<List<UsuarioComunidad>>() {
            @Override
            public List<UsuarioComunidad> call() throws Exception
            {
                return userComuDaoRemote.seeUserComusByComu(entityId);
            }
        });
    }

    static Single<Comunidad> comunidad(final long comunidadId)
    {
        Timber.d("comunidad()");
        return Single.fromCallable(new Callable<Comunidad>() {
            @Override
            public Comunidad call() throws Exception
            {
                return comunidadDao.getComuData(comunidadId);
            }
        });
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(final long entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(
                listByEntityId(entityId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ObserverSingleList<>(this))
        );
    }

    @Override
    public void onSuccessLoadItemsById(List<UsuarioComunidad> itemList)
    {
        Timber.d("onSuccessLoadItemsById()");
        viewerList.processLoadedItemsinView(itemList);
    }

    boolean comunidadData(long comunidadId)
    {
        Timber.d("getNombreComunidad()");
        return subscriptions.add(
                comunidad(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Comunidad>() {
                            @Override
                            public void onSuccess(Comunidad comunidad)
                            {
                                Timber.d("onSuccess()");
                                onSuccessComunidadData(comunidad); }

                            @Override
                            public void onError(Throwable e)
                            {
                                Timber.d("onErrorCtrl()");
                                onErrorCtrl(e);
                            }
                        })
        );
    }

    @SuppressWarnings("WeakerAccess")
    void onSuccessComunidadData(Comunidad comunidad)
    {
        Timber.d("onSuccessComunidadData()");
        viewerList.setNombreComuViewText(comunidad.getNombreComunidad());
    }
}
