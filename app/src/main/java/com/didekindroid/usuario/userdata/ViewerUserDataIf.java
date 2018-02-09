package com.didekindroid.usuario.userdata;

import android.view.View;

import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.usuario.dao.CtrlerUsuarioIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:25
 */

interface ViewerUserDataIf extends ViewerIf<View, CtrlerUsuarioIf> {

    boolean checkUserData();

    UserChangeToMake whatDataChangeToMake();

    boolean modifyUserData(UserChangeToMake userChangeToMake);

    void processBackUserDataLoaded(Usuario usuario);

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,;
    }
}
