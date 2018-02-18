package com.didekindroid.lib_one.usuario;

import android.view.View;

import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.usuario.dao.CtrlerUsuarioIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:25
 */

public interface ViewerUserDataIf extends ViewerIf<View, CtrlerUsuarioIf> {

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
