package com.didekindroid.usuariocomunidad.repository;

import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 19:14
 */

public class UserComuObservable {

    // ................................... OBSERVABLES .....................................

    public static Single<List<Comunidad>> comunidadesByUser()
    {
        Timber.d("comunidadesByUser()");
        return fromCallable(userComuDao::getComusByUser);
    }

    public static Single<Integer> comunidadModificada(final Comunidad comunidad)
    {
        Timber.d("comunidadModificada()");
        return fromCallable(() -> {
            Timber.d("call()");
            return userComuDao.modifyComuData(comunidad);
        });
    }

    public static Maybe<UsuarioComunidad> comunidadByUserAndComu(final Comunidad comunidad)
    {
        Timber.d("comunidadByUserAndComu()");
        return Maybe.fromCallable(() -> userComuDao.getUserComuByUserAndComu(comunidad.getC_Id()));
    }
}
