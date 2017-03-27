package com.didekindroid.api;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:27
 */
public interface ControllerIf {

    CompositeDisposable getSubscriptions();

    void onErrorCtrl(Throwable e);

    int clearSubscriptions();

    ViewerIf getViewer();
}
