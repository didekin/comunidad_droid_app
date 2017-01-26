package com.didekindroid.usuario.userdata;

import com.didekin.usuario.Usuario;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface UserDataControllerIf {

    boolean checkLoginData();

    void loadUserData();

    void modifyUserData(UserChangeToMake userChangeToMake);

    CompositeDisposable getSubscriptions();

    void processBackUserDataLoaded(Usuario usuario);

    void processBackUserDataUpdated(boolean toInitTokenCache);

    void processBackErrorInReactor(Throwable e);

    UserChangeToMake whatDataChangeToMake();

    void processBackGenericUpdated();

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,
        ;
    }
}
