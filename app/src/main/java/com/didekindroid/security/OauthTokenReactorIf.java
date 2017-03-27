package com.didekindroid.security;


import com.didekinlib.model.usuario.Usuario;

import io.reactivex.observers.DisposableCompletableObserver;

/**
 * User: pedro@didekin
 * Date: 23/01/17
 * Time: 12:14
 */
public interface OauthTokenReactorIf {

    void updateTkAndCacheFromUser(Usuario newUser);

    DisposableCompletableObserver updateTkCacheFromRefreshTk(String refreshToken);
}
