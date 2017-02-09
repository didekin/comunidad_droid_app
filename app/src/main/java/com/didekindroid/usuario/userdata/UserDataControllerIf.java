package com.didekindroid.usuario.userdata;


import com.didekinlib.model.usuario.Usuario;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface UserDataControllerIf {

    boolean checkLoginData();

    void loadUserData();

    boolean modifyUserData(UserChangeToMake userChangeToMake);

    CompositeDisposable getSubscriptions();

    void processBackUserDataLoaded(Usuario usuario);

    void processBackErrorInReactor(Throwable e);

    UserChangeToMake whatDataChangeToMake();

    void processBackUserModified();

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,;
    }
}
