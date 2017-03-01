package com.didekindroid.usuario.userdata;

import android.view.View;

import com.didekindroid.ManagerIf.ViewerIf;
import com.didekinlib.model.usuario.Usuario;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:25
 */

interface ViewerUserDataIf<T extends View, B> extends ViewerIf<T,B> {

    void initUserDataInView();

    String[] getDataChangedFromView();

    boolean checkUserData();

    UserChangeToMake whatDataChangeToMake();

    boolean modifyUserData(UserChangeToMake userChangeToMake);

    void processBackUsuarioInView(Usuario usuario);

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,;
    }
}
