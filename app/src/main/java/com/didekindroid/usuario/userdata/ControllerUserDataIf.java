package com.didekindroid.usuario.userdata;


import com.didekindroid.ManagerIf.ControllerIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface ControllerUserDataIf extends ControllerIf {

    void loadUserData();

    boolean modifyUser(Usuario oldUser, Usuario newUser);

    void processBackUserDataLoaded(Usuario usuario);

    void processBackUserModified();

    // ................. REACTOR ....................

    interface ReactorUserDataIf {

        boolean loadUserData(ControllerUserDataIf controller);

        boolean modifyUser(ControllerUserDataIf controller, Usuario oldUser, Usuario newUser);
    }
}
