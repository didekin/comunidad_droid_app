package com.didekindroid.api;

import com.didekindroid.security.IdentityCacher;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:27
 */
public interface CtrlerIdentityIf extends ControllerIf {

    boolean isRegisteredUser();

    void updateIsRegistered(boolean isRegisteredUser);

    IdentityCacher getIdentityCacher();
}
