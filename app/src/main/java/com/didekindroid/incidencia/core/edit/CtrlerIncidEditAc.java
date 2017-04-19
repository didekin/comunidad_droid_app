package com.didekindroid.incidencia.core.edit;

import com.didekindroid.api.Controller;
import com.didekindroid.incidencia.IncidObservable;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:24
 */

@SuppressWarnings("WeakerAccess")
class CtrlerIncidEditAc extends Controller {

    ViewerIncidEditAc viewerIncidEditAc;

    CtrlerIncidEditAc(ViewerIncidEditAc viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerIncidEditAc(ViewerIncidEditAc viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
        viewerIncidEditAc = viewer;
    }

    // .................................... INSTANCE METHODS .................................

    boolean seeResolucion(final long incidenciaId, final int resourceIdItemMn)
    {
        Timber.d("checkResolucion()");
        return subscriptions.add(
                IncidObservable.resolucion(incidenciaId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Resolucion>() {
                            @Override
                            public void onSuccess(Resolucion resolucion)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidEditAc.onSuccessSeeResolucion(resolucion, resourceIdItemMn);
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
