package com.didekindroid.usuariocomunidad.listbyuser;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.usuariocomunidad.repository.UserComuDao;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

class CtrlerSeeUserComuByUser extends CtrlerSelectList<UsuarioComunidad> {

    private final UserComuDao userComuDaoRemote;

    CtrlerSeeUserComuByUser()
    {
        super();
        userComuDaoRemote = UserComuDao.userComuDao;
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<UsuarioComunidad>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return getSubscriptions().add(
             userComuDaoRemote.seeUserComusByUser()
             .subscribeOn(io())
             .observeOn(mainThread())
             .subscribeWith(observer)
        );
    }
}
