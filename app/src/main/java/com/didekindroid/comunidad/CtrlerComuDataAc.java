package com.didekindroid.comunidad;

import com.didekindroid.api.Controller;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComunidadAssertionMsg.comuData_should_be_modified;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuariocomunidad.dao.UserComuObservable.comunidadModificada;
import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 08/05/17
 * Time: 14:25
 */

class CtrlerComuDataAc extends Controller {

    CtrlerComuDataAc(ViewerComuDataAc viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerComuDataAc(ViewerComuDataAc viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
    }

    // .................................... INSTANCE METHODS .................................

    boolean modifyComunidadData(Comunidad comunidad)
    {
        Timber.d("modifyComunidadData()");
        return subscriptions.add(
                comunidadModificada(comunidad)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new DisposableSingleObserver<Integer>() {

                            @Override
                            public void onSuccess(Integer rowsUpdated)
                            {
                                Timber.d("onSuccess()");
                                assertTrue(rowsUpdated == 1, comuData_should_be_modified);
                                ViewerComuDataAc.class.cast(viewer).onSuccessModifyComunidad();
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
