package com.didekindroid.comunidad;

import android.os.Bundle;

import com.didekindroid.api.Controller;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.ComunidadObservable.comunidad;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 15:42
 */

class CtrlerRegComuFr extends Controller {

    CtrlerRegComuFr(ViewerRegComuFr viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerRegComuFr(ViewerRegComuFr viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

    // .................................... INSTANCE METHODS .................................

    boolean loadComunidadData(long comunidadId, final Bundle savedState)
    {
        Timber.d("loadComunidadData()");
        return subscriptions.add(
                comunidad(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Comunidad>() {

                            @Override
                            public void onSuccess(Comunidad comunidad)
                            {
                                Timber.d("onSuccess()");
                                ViewerRegComuFr.class.cast(viewer).onSuccessLoadComunidad(comunidad, savedState);
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                Timber.d("onError()");
                                onErrorCtrl(e);
                            }
                        })
        );
    }
}
