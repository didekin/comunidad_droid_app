package com.didekindroid.incidencia.core;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 13/04/17
 * Time: 11:42
 */
public abstract class IncidRegEditObserver extends DisposableSingleObserver<Integer> {

    private final CtrlerIncidRegEditFr controller;
    private final ViewerIncidRegEdit viewer;

    protected IncidRegEditObserver(CtrlerIncidRegEditFr controller, ViewerIncidRegEdit viewer)
    {
        this.controller = controller;
        this.viewer = viewer;
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError()");
        controller.onErrorCtrl(e);
    }
}
