package com.didekindroid.security;


import com.didekindroid.api.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 23/01/17
 * Time: 12:14
 */
public interface CtrlerAuthTokenIf extends ControllerIf {

    void updateTkAndCacheFromUser(Usuario newUser);

    boolean updateTkCacheFromRefreshTk(String refreshToken);

    void refreshAccessToken();
}
