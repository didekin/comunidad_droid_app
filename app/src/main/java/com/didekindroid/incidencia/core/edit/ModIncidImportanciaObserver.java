package com.didekindroid.incidencia.core.edit;

import com.didekindroid.api.ViewerIf;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 17:22
 */

class ModIncidImportanciaObserver<T extends ViewerIf & ModIncidImportanciaCallableBack> extends DisposableSingleObserver<Integer> {

    private final T viewerCallBack;

    ModIncidImportanciaObserver(T viewerCallBack)
    {
        this.viewerCallBack = viewerCallBack;
    }

    @Override
    public void onSuccess(Integer rowModified)
    {
        Timber.d("onSuccess()");
        viewerCallBack.onSuccessModifyIncidImportancia(rowModified);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewerCallBack.onErrorInObserver(e);
    }
}
