package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.view.View;

import com.didekindroid.api.CtrlerIdentity;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_registered;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 31/03/17
 * Time: 12:40
 */

@SuppressWarnings({"AnonymousInnerClassMayBeStatic", "WeakerAccess"})
public class CtrlerIncidRegAc extends CtrlerIdentity<View> {

    CtrlerIncidRegAc(ViewerIncidRegAc viewer)
    {
        this(viewer, TKhandler);
    }

    public CtrlerIncidRegAc(ViewerIncidRegAc viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

    // .................................... OBSERVABLES .................................

    static Single<Integer> incidenciaRegistered(final IncidImportancia incidImportancia)
    {
        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception
            {
                return incidenciaDao.regIncidImportancia(incidImportancia);
            }
        });
    }

    // .................................... INSTANCE METHODS .................................

    public boolean registerIncidencia(IncidImportancia incidImportancia)
    {
        Timber.d("registerIncidencia()");
        return subscriptions.add(
                incidenciaRegistered(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer rowInserted)
                            {
                                Timber.d("onSuccess()");
                                assertTrue(rowInserted == 2, incid_importancia_should_be_registered);
                                onSuccessRegisterIncidencia();
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

    void onSuccessRegisterIncidencia()
    {
        Timber.d("onSuccessRegisterIncidencia()");
        viewer.replaceRootView(new Bundle());
    }
}
