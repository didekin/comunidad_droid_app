package com.didekindroid.api;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 20/03/17
 * Time: 14:03
 */
public class ObserverSingleSelectedItem<B> extends DisposableSingleObserver<B> {

    private final CtrlerSelectableItemIf<?, B> controller;

    public ObserverSingleSelectedItem(CtrlerSelectableItemIf<?, B> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(B bundle)
    {
        Timber.d("onSuccess()");
        controller.onSuccessSelectedItem(bundle);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        controller.onErrorCtrl(e);
    }
}
