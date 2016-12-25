package com.didekinaar;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.exception.UiException;

import rx.Subscriber;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 15:34
 */

@SuppressWarnings("AbstractClassNeverImplemented")
public abstract class ActivitySubscriber<T,R extends Activity> extends Subscriber<T> {

    protected final R activity;

    protected ActivitySubscriber(R activity)
    {
        this.activity = activity;
    }

    @Override
    public void onCompleted()
    {   // TODO: test.
        Timber.d("onCompleted()");
        unsubscribe();
    }

    @Override
    public void onError(Throwable e)
    {   // TODO: test.Todos los 'processMe' de didekinaar verificar que el mensaje en UiException es GENERIC_ERROR.
        Timber.d("onError");
        if (e instanceof UiException) {
            ((UiException) e).processMe(activity, new Intent());
        }
    }

}
