package com.didekindroid.incidencia.core.edit;

import android.view.View;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaModified;
import static com.didekindroid.incidencia.IncidObservable.incidenciaDeleted;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:08
 */
@SuppressWarnings({"WeakerAccess", "AnonymousInnerClassMayBeStatic"})
class CtrlerIncidEditMaxFr extends CtrlerIdentity<View> {

    ViewerIncidEditMaxFr viewerIncidEditMaxFr;

    protected CtrlerIncidEditMaxFr(ViewerIncidEditMaxFr viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerIncidEditMaxFr(ViewerIncidEditMaxFr viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
        viewerIncidEditMaxFr = viewer;
    }

    // .................................... INSTANCE METHODS .................................

    public boolean modifyIncidImportancia(IncidImportancia newIncidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return subscriptions.add(
                incidImportanciaModified(newIncidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer rowsModified)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidEditMaxFr.onSuccessModifyIncidImportancia(rowsModified);
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

    public boolean eraseIncidencia(Incidencia incidencia)
    {
        Timber.d("eraseIncidencia()");
        return subscriptions.add(
                incidenciaDeleted(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer rowsDeleted)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidEditMaxFr.onSuccessEraseIncidencia(rowsDeleted);
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
