package com.didekindroid.api;

import com.didekindroid.security.IdentityCacher;

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

    boolean isRegisteredUser();

    void updateIsRegistered(boolean isRegisteredUser);

    IdentityCacher getIdentityCacher();
}