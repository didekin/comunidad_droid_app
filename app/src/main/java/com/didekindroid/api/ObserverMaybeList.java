package com.didekindroid.api;

import com.didekindroid.exception.UiException;

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

    private final CtrlerListIf<E> controller;

    public ObserverMaybeList(CtrlerListIf<E> controller)
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

    @Override
    public void onComplete()
    {
        Timber.d("onComplete(), Thread for subscriber: %s", Thread.currentThread().getName());
        // Do nothing in the controller.
    }
}
