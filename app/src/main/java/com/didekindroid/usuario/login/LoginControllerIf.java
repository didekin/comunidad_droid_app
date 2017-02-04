package com.didekindroid.usuario.login;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 10:30
 */
interface LoginControllerIf {
    boolean checkLoginData();
    void validateLoginRemote();
    void processBackLoginRemote(Boolean isLoginOk);
    void processBackSendPassword(Boolean isSendPassword);
    void doDialogPositiveClick();
    void doDialogNegativeClick();
    CompositeDisposable getSubscriptions();
    void processBackErrorInReactor(Throwable e);
}
