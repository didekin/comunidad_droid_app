package com.didekindroid.usuario.delete;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:46
 */
interface DeleteMeControllerIf {
    boolean unregisterUser();
    CompositeDisposable getSubscriptions();
    void processBackDeleteMeRemote(Boolean isDeleted);
    void processErrorInReactor(Throwable e);
}
