package com.didekindroid.api;

import io.reactivex.disposables.Disposable;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 10:39
 */
public class DisposableMock implements Disposable {
    @Override
    public void dispose()
    {
    }

    @Override
    public boolean isDisposed()
    {
        return false;
    }
}
