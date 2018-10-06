package com.didekindroid.usuariocomunidad.repository;

import com.didekindroid.lib_one.api.Controller;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 12:13
 */

public class CtrlerUsuarioComunidad extends Controller {

    private final UserComuDao userComuDaoRemote;

    public CtrlerUsuarioComunidad()
    {
        super();
        userComuDaoRemote = userComuDao;
    }

    public boolean deleteUserComu(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
    {
        Timber.d("deleteUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .deleteUserComu(comunidad.getC_Id())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean getUserComuByUserAndComu(DisposableMaybeObserver<UsuarioComunidad> observer, Comunidad comunidad)
    {
        Timber.d("getUserComuByUserAndComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .getUserComuByUserAndComu(comunidad.getC_Id())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean isOldestOrAdmonUserComu(DisposableSingleObserver<Boolean> observer, Comunidad comunidad)
    {
        Timber.d("isOldestOrAdmonUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .isOldestOrAdmonUserComu(comunidad.getC_Id())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean modifyUserComu(DisposableSingleObserver<Integer> observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("modifyUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .modifyUserComu(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regComuAndUserAndUserComu(DisposableCompletableObserver observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regComuAndUserAndUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .regComuAndUserAndUserComu(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regComuAndUserComu(DisposableCompletableObserver observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regComuAndUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .regComuAndUserComu(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regUserAndUserComu(DisposableCompletableObserver observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regUserAndUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .regUserAndUserComu(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean regUserComu(DisposableCompletableObserver observer, UsuarioComunidad usuarioComunidad)
    {
        Timber.d("regUserComu()");
        return getSubscriptions().add(
                userComuDaoRemote
                        .regUserComu(usuarioComunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
