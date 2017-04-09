package com.didekindroid.incidencia.core.reg;

import android.view.View;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.incidencia.IncidObservable;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 12:40
 */
@SuppressWarnings({"AnonymousInnerClassMayBeStatic", "WeakerAccess"})
class CtrlerIncidRegAc extends CtrlerIdentity<View> {

    CtrlerIncidRegAc(ViewerIncidRegAc viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerIncidRegAc(ViewerIncidRegAc viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

    // .................................... INSTANCE METHODS .................................

    boolean registerIncidencia(IncidImportancia incidImportancia)
    {
        Timber.d("registerIncidencia()");
        return subscriptions.add(
                IncidObservable.incidenciaRegistered(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer rowInserted)
                            {
                                Timber.d("onSuccess()");
                                ViewerIncidRegAc.class.cast(viewer).onSuccessRegisterIncidencia(rowInserted);
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
