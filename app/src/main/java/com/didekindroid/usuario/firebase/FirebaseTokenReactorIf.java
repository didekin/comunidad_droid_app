package com.didekindroid.usuario.firebase;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 09:58
 */
public interface FirebaseTokenReactorIf {
    CompositeDisposable checkGcmToken(CompositeDisposable subscriptions);
    void checkGcmTokenSync();
}
