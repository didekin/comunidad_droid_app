package com.didekindroid.usuario.userdata;

import com.didekin.usuario.Usuario;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface UserDataControllerIf {

    void loadUserData();

    void modifyUserData(UserChangeToMake userChangeToMake);

    void modifyOnlyAlias();

    CompositeDisposable getSubscriptions();

    void processBackGetUserData(Usuario usuario);

    void processBackErrorInReactor(Throwable e);

    boolean checkLoginData();

    UserChangeToMake whatDataChangeToMake();

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,
        ;
    }
}
