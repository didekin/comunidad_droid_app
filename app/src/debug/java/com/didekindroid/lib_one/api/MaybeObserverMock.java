package com.didekindroid.lib_one.api;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;

/**
 * User: pedro@didekin
 * Date: 20/06/17
 * Time: 10:58
 */

public class MaybeObserverMock<T> extends DisposableMaybeObserver<T> {

    @Override
    public void onSuccess(@NonNull T t)
    {
    }

    @Override
    public void onError(@NonNull Throwable e)
    {
    }

    @Override
    public void onComplete()
    {
    }
}
