package com.didekindroid.usuario.password;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 15:00
 */
interface PasswordChangeViewIf {
    String[] getPswdDataFromView();
    boolean checkLoginData();
    void changePasswordInRemote();
}
