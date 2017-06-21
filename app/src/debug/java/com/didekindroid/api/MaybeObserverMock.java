package com.didekindroid.api;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableMaybeObserver;

import static com.didekindroid.util.UIutils.assertTrue;

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
        assertTrue(false, "fail for tests.");
    }

    @Override
    public void onComplete()
    {
    }
}
