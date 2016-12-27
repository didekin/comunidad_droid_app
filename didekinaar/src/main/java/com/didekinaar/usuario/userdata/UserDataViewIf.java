package com.didekinaar.usuario.userdata;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:25
 */

interface UserDataViewIf {

    void initUserDataInView();
    UserChangeToMake getDataChangedFromAcView();

    enum UserChangeToMake {
        alias_only,
        userName,
        nothing,
        ;
    }
}
