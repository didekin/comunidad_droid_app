package com.didekindroid.api;

import com.didekindroid.exception.UiException;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 12:03
 */
public class ObserverSingleList<E extends Serializable> extends DisposableSingleObserver<List<E>> {

    private final CtrlerListIf<E> controller;

    public ObserverSingleList(CtrlerListIf<E> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(List<E> list)
    {
        Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.onSuccessLoadItemsById(list);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl(), Thread for subscriber: %s", Thread.currentThread().getName());
        if (e instanceof UiException) {
            Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
        }
        controller.onErrorCtrl(e);
    }
}
