package com.didekindroid.usuario.password;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 24/12/16
 * Time: 14:45
 */
interface PasswordChangeControllerIf {
    void processBackChangedPswdRemote();
    CompositeDisposable getSubscriptions();
    void processErrorInReactor(Throwable e);
}
