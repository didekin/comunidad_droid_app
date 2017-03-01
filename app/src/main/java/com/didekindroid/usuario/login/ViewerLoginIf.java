package com.didekindroid.usuario.login;

import android.view.View;

import com.didekindroid.ManagerIf.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 11:11
 */
interface ViewerLoginIf<T extends View,B> extends ViewerIf<T,B>{

    String EMAIL_DIALOG_ARG = "email";

    boolean checkLoginData();

    void showDialog(String userName);

    String[] getLoginDataFromView();

    void processLoginBackInView(boolean isLoginOk);

    void processBackSendPswdInView(boolean isSendPassword);

    void doDialogNegativeClick();
}
