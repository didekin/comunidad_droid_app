package com.didekindroid.api;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:56
 */
public class SingleObserverSelectionList<E extends Serializable> extends DisposableSingleObserver<List<E>> {

    private final CtrlerSelectionListIf<E> controller;

    public SingleObserverSelectionList(CtrlerSelectionListIf<E> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(List<E> items)
    {
        Timber.d("onSuccess()");
        controller.onSuccessLoadItemsInList(items);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl()");
        controller.onErrorCtrl(e);
    }
}
