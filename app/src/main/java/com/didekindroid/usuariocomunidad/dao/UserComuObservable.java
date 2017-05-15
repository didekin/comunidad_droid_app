package com.didekindroid.usuariocomunidad.dao;

import com.didekinlib.model.comunidad.Comunidad;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 14/02/17
 * Time: 19:14
 */

public class UserComuObservable {

    // ................................... OBSERVABLES .....................................

    public static Single<List<Comunidad>> comunidadesByUser(){

        return fromCallable(new Callable<List<Comunidad>>() {
            @Override
            public List<Comunidad> call() throws Exception
            {
                return userComuDaoRemote.getComusByUser();
            }
        });
    }

    public static Single<Integer> comunidadModificada(final Comunidad comunidad){
        Timber.d("comunidadModificada()");
        return fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                Timber.d("call()");
                return userComuDaoRemote.modifyComuData(comunidad);
            }
        });
    }
}
