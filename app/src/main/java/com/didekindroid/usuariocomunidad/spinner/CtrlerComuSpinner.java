package com.didekindroid.usuariocomunidad.spinner;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.usuariocomunidad.repository.UserComuDao;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:11
 */

public class CtrlerComuSpinner extends CtrlerSelectList<Comunidad> {

    private final UserComuDao userComuDaoRemote;

    CtrlerComuSpinner()
    {
        super();
        userComuDaoRemote = userComuDao;
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Comunidad>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        Timber.d("comunidadesByUser()");
        return getSubscriptions().add(
                userComuDaoRemote.getComusByUser()
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
