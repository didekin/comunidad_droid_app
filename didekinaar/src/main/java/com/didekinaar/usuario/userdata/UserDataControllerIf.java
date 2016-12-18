package com.didekinaar.usuario.userdata;

import com.didekinaar.usuario.UsuarioBean;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 17:37
 */
interface UserDataControllerIf {
    UsuarioBean makeUserBeanFromUserDataAcView();
    void loadUserData();
    void modifyUserData();
}
