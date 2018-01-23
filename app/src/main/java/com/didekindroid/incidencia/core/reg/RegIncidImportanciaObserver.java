package com.didekindroid.incidencia.core.reg;

import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 17:17
 */
public class RegIncidImportanciaObserver<T extends ViewerIncidRegAc> extends
        DisposableSingleObserver<Integer> {

    private final T viewerCallBack;
    private final Comunidad comunidad;

    RegIncidImportanciaObserver(T viewerCallBack, Comunidad comunidad)
    {
        this.viewerCallBack = viewerCallBack;
        this.comunidad = comunidad;
    }

    @Override
    public void onSuccess(Integer rowInserted)
    {
        Timber.d("onSuccess()");
        viewerCallBack.onSuccessRegisterIncidImportancia(comunidad);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewerCallBack.onErrorInObserver(e);
    }
}
