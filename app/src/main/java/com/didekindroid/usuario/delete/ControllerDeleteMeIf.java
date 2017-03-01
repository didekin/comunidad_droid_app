package com.didekindroid.usuario.delete;

import com.didekindroid.ManagerIf.ControllerIf;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:46
 */
interface ControllerDeleteMeIf extends ControllerIf {

    boolean unregisterUser();

    void processBackDeleteMeRemote(boolean isDeleted);

    // ................. REACTOR ....................

    interface ReactorDeleteMeIf {
        boolean deleteMeInRemote(ControllerDeleteMeIf controller);
    }
}
