package com.didekindroid.api;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 11/05/17
 * Time: 12:38
 */
class ObserverSelectionList<E extends Serializable> extends
        DisposableObserver<List<E>> {

    private final CtrlerSelectionListIf<E> controller;

    ObserverSelectionList(CtrlerSelectionListIf<E> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onNext(List<E> municipios)
    {
        Timber.d("onNext()");
        controller.onSuccessLoadItemsInList(municipios);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl()");
        controller.onErrorCtrl(e);
    }

    @Override
    public void onComplete()
    {
        Timber.d("onComplete()");
    }
}
