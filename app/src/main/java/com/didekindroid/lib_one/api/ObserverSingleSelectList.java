package com.didekindroid.lib_one.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 11/05/17
 * Time: 12:38
 */
public class ObserverSingleSelectList<H extends ViewerSelectListIf<?, ?, E>, E extends Serializable> extends
        DisposableSingleObserver<List<E>> {

    private final H viewer;

    public ObserverSingleSelectList(H viewer)
    {
        this.viewer = viewer;
    }

    @Override
    public void onSuccess(List<E> list)
    {
        Timber.d("onSuccess()");
        List<E> newList;
        if (list == null){
            newList = new ArrayList<>(0);
        } else {
            newList = Collections.unmodifiableList(list);
        }
        viewer.onSuccessLoadItemList(newList);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onErrorObserver()");
        viewer.onErrorInObserver(e);
    }
}
