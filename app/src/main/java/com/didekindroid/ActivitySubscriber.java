package com.didekindroid;

import android.app.Activity;
import android.content.Intent;

import com.didekindroid.exception.UiException;

import rx.Subscriber;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 15:34
 */
public abstract class ActivitySubscriber<T,R extends Activity> extends Subscriber<T> {

    protected final R activity;

    protected ActivitySubscriber(R activity)
    {
        this.activity = activity;
    }

    @Override
    public void onNext(T item)
    {
        Timber.d("onNext");
    }

    @Override
    public void onCompleted()
    {
        Timber.d("onCompleted()");
        unsubscribe();
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError");
        if (e instanceof UiException) {
            ((UiException) e).processMe(activity, new Intent());
        }
    }

    public R getActivity()
    {
        return activity;
    }
}
