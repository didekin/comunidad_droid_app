package com.didekindroid.usuario.login;

import android.support.v7.app.AppCompatDialog;
import android.view.View;

import com.didekindroid.api.ViewerIf;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekinlib.model.usuario.Usuario;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * User: pedro@didekin
 * Date: 29/11/16
 * Time: 11:11
 */
interface ViewerLoginIf extends ViewerIf<View,CtrlerUsuario> {

    boolean checkLoginData();

    void processLoginBackInView(boolean isLoginOk);

    void doDialogPositiveClick(Usuario serializable);

    AppCompatDialog doDialogInViewer(LoginAc.PasswordMailDialog dialogFragment);

    void doDialogNegativeClick();

    void processBackSendPswdInView(boolean isSendPassword);

    AtomicInteger getCounterWrong();
}
