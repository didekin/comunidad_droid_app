package com.didekindroid.lib_one.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 10:06
 */
public class ObserverSingleList<H extends ViewerListIf<?,?, E>,E extends Serializable> extends DisposableSingleObserver<List<E>> {

    private final H viewer;

    public ObserverSingleList(H viewer)
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
