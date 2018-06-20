package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.usuariocomunidad.repository.UserComuDao;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 15:42
 */

class CtrlerComunidad extends Controller {

    private final ComunidadDao comunidadDaoRemote;
    private final UserComuDao userComuDaoRemote;

    CtrlerComunidad()
    {
        super();
        comunidadDaoRemote = comunidadDao;
        userComuDaoRemote = userComuDao;
    }

    // .................................... INSTANCE METHODS .................................

    boolean getComunidadData(DisposableSingleObserver<Comunidad> observer, long comunidadId)
    {
        Timber.d("getComunidadData()");
        return getSubscriptions().add(
                comunidadDaoRemote
                        .getComuData(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    boolean searchInComunidades(DisposableSingleObserver<List<Comunidad>> observer, Comunidad comunidadToSearch)
    {
        Timber.d("searchInComunidades()");
        return getSubscriptions().add(
                comunidadDaoRemote
                        .searchInComunidades(comunidadToSearch)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean modifyComunidadData(DisposableSingleObserver<Integer> observer, Comunidad comunidad)
    {
        Timber.d("modifyComunidadData()");
        return getSubscriptions().add(
                userComuDaoRemote.modifyComuData(comunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }

    public boolean getUserComu(DisposableMaybeObserver<UsuarioComunidad> observer, Comunidad comunidad)
    {
        Timber.d("getUserComu()");      // TODO: test.
        return getSubscriptions().add(
                userComuDaoRemote
                        .getUserComuByUserAndComu(comunidad.getC_Id())
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(observer)
        );
    }
}
