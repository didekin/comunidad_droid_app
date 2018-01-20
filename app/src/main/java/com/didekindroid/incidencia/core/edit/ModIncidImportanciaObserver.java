package com.didekindroid.incidencia.core.edit;

import com.didekinlib.model.comunidad.Comunidad;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 17:22
 */

class ModIncidImportanciaObserver<T extends ViewerIncidEditFr> extends DisposableSingleObserver<Integer> {

    private final T viewerCallBack;
    private final Comunidad comunidad;

    ModIncidImportanciaObserver(T viewerCallBack, Comunidad comunidad)
    {
        this.viewerCallBack = viewerCallBack;
        this.comunidad = comunidad;
    }

    @Override
    public void onSuccess(Integer rowModified)
    {
        Timber.d("onSuccess()");
        viewerCallBack.onSuccessModifyIncidImportancia(comunidad);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewerCallBack.onErrorInObserver(e);
    }
}
