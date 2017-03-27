package com.didekindroid.api;

import com.didekindroid.exception.UiException;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 20/03/17
 * Time: 14:03
 */
public class ObserverSingleListSelected<B> extends DisposableSingleObserver<B> {

    private final CtrlerSelectableListIf<?,B> controller;

    public ObserverSingleListSelected(CtrlerSelectableListIf<?,B> controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(B bundle)
    {
        Timber.d("onSuccess()");
        controller.onSuccessDealSelectedItem(bundle);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorCtrl()");
        if (e instanceof UiException) {
            Timber.d("UiException message: %s", ((UiException) e).getErrorBean().getMessage());
        }
        controller.onErrorCtrl(e);
    }
}
