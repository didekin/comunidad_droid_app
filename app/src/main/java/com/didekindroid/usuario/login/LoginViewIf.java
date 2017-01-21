package com.didekindroid.usuario.login;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 11:11
 */
interface LoginViewIf {

    String EMAIL_DIALOG_ARG = "email";

    void showDialog(String userName);

    String[] getLoginDataFromView();
}
