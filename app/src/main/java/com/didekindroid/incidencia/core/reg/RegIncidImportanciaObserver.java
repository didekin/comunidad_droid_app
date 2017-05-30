package com.didekindroid.incidencia.core.reg;

import com.didekindroid.api.ViewerIf;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 17:17
 */
public class RegIncidImportanciaObserver<T extends ViewerIf & RegIncidImportanciaCallableBack> extends
        DisposableSingleObserver<Integer> {

    private final T viewerCallBack;

    public RegIncidImportanciaObserver(T viewerCallBack)
    {
        this.viewerCallBack = viewerCallBack;
    }

    @Override
    public void onSuccess(Integer rowInserted)
    {
        Timber.d("onSuccess()");
        viewerCallBack.onSuccessRegisterIncidImportancia(rowInserted);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewerCallBack.onErrorInObserver(e);
    }
}
