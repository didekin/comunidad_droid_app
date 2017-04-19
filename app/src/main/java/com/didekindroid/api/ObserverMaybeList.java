package com.didekindroid.api;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 12:03
 */
public class ObserverMaybeList<E extends Serializable> extends DisposableMaybeObserver<List<E>> {

    private final CtrlerSelectionListIf<E> controller;

    public ObserverMaybeList(CtrlerSelectionListIf<E> controller)
    {
        this.controller = controller;
    }


    @Override
    public void onSuccess(List<E> list)
    {
        Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.onSuccessLoadItemsInList(list);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.onErrorCtrl(e);
    }

    @Override
    public void onComplete()
    {
        Timber.d("onComplete(), Thread for subscriber: %s", Thread.currentThread().getName());
        // Do nothing in the controller.
    }
}
