package com.didekindroid.lib_one.api;

import android.os.Bundle;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 20/03/17
 * Time: 14:03
 */
public class ObserverSingleSelectItem<H extends ViewerSelectListIf<?, ?, E>, E extends Serializable> extends DisposableSingleObserver<Bundle> {

    private final H viewer;

    public ObserverSingleSelectItem(H viewer)
    {
        this.viewer = viewer;
    }

    @Override
    public void onSuccess(Bundle bundle)
    {
        Timber.d("onSuccess()");
        viewer.onSuccessLoadSelectedItem(bundle);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        viewer.onErrorInObserver(e);
    }
}
