package com.didekindroid.comunidad;

import com.didekinlib.model.comunidad.Comunidad;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadDao.comunidadDao;
import static io.reactivex.Single.fromCallable;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 16:31
 */

class ComunidadObservable {

    public static Single<Comunidad> comunidad(final long comunidadId)
    {
        Timber.d("comunidad()");
        return fromCallable(new Callable<Comunidad>() {
            @Override
            public Comunidad call() throws Exception
            {
                Timber.d("call()");
                return comunidadDao.getComuData(comunidadId);
            }
        });
    }
}
