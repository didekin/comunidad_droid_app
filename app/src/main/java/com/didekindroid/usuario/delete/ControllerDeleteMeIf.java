package com.didekindroid.usuario.delete;

import com.didekindroid.ManagerIf;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:46
 */
interface ControllerDeleteMeIf extends ManagerIf.ControllerIdentityIf {

    boolean unregisterUser();

    void processBackDeleteMeRemote(boolean isDeleted);

    // ................. REACTOR ....................

    interface ReactorDeleteMeIf {
        boolean deleteMeInRemote(ControllerDeleteMeIf controller);
    }
}
