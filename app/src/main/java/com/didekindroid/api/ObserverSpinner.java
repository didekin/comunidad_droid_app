package com.didekindroid.api;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:56
 */
public class ObserverSpinner<E> extends DisposableSingleObserver<List<E>> {

    private final CtrlerSpinnerIf<E> controller;

    public ObserverSpinner(CtrlerSpinnerIf<E> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(List<E> items)
    {
        Timber.d("onSuccess()");
        controller.onSuccessLoadDataInSpinner(items);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl()");
        controller.onErrorCtrl(e);
    }
}
