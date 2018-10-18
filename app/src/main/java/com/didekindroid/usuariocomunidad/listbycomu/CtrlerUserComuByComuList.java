package com.didekindroid.usuariocomunidad.listbycomu;

import com.didekindroid.comunidad.ComunidadDao;
import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.usuariocomunidad.repository.UserComuDao;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

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
public class CtrlerUserComuByComuList extends CtrlerSelectList<UsuarioComunidad> {

    private final UserComuDao userComuDaoRemote;
    private final ComunidadDao comunidadDaoRemote;

    CtrlerUserComuByComuList()
    {
        super();
        userComuDaoRemote = userComuDao;
        comunidadDaoRemote = comunidadDao;
    }

    // .................................... INSTANCE METHODS .................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<UsuarioComunidad>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return getSubscriptions().add(
                userComuDaoRemote.seeUserComusByComu(entityId[0])
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean comunidadData(DisposableSingleObserver<Comunidad> observer, long comunidadId)
    {
        Timber.d("comunidad()");
        return getSubscriptions().add(
                comunidadDaoRemote.getComuData(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
