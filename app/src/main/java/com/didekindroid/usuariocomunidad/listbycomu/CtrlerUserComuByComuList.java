package com.didekindroid.usuariocomunidad.listbycomu;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 15/03/17
 * Time: 10:59
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic"})
public class CtrlerUserComuByComuList extends CtrlerSelectList<UsuarioComunidad> {

    // .................................... OBSERVABLES .................................

    static Single<List<UsuarioComunidad>> listByEntityId(final long entityId)
    {
        Timber.d("listByEntityId()");
        return Single.fromCallable(() -> userComuDao.seeUserComusByComu(entityId));
    }

    static Single<Comunidad> comunidad(final long comunidadId)
    {
        Timber.d("comunidad()");
        return Single.fromCallable(() -> comunidadDao.getComuData(comunidadId));
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<UsuarioComunidad>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return getSubscriptions().add(
                listByEntityId(entityId[0])
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean comunidadData(DisposableSingleObserver<Comunidad> observer, long comunidadId)
    {
        Timber.d("getNombreComunidad()");
        return getSubscriptions().add(
                comunidad(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
