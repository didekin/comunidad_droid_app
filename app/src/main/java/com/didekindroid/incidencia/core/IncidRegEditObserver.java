package com.didekindroid.incidencia.core;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 13/04/17
 * Time: 11:42
 */
abstract class IncidRegEditObserver extends DisposableSingleObserver<Integer> {

    private final CtrlerIncidRegEditFr controller;

    IncidRegEditObserver(CtrlerIncidRegEditFr controller)
    {
        this.controller = controller;
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        controller.onErrorCtrl(e);
    }
}
