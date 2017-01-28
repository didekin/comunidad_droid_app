package com.didekindroid.security;


import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 23/01/17
 * Time: 12:14
 */
public interface OauthTokenReactorIf {

    void updateTkAndCacheFromUser(Usuario newUser);
}
