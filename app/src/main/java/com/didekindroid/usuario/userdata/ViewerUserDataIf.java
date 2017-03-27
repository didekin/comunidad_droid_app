package com.didekindroid.usuario.userdata;

import android.content.Intent;
import android.view.View;

import com.didekindroid.api.ViewerIf;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:25
 */

interface ViewerUserDataIf extends ViewerIf<View,CtrlerUserDataIf> {

    AtomicReference<Intent> getIntentForMenu();

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
