package com.didekindroid.lib_one.api;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 06/06/17
 * Time: 13:14
 */

public class SingleObserverMock<T> extends DisposableSingleObserver<T> {

    @Override
    public void onSuccess(T t)
    {
    }

    @Override
    public void onError(Throwable e)
    {
    }
}
