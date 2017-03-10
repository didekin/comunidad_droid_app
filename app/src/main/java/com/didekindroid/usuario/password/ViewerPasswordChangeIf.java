package com.didekindroid.usuario.password;

import android.view.View;

import com.didekindroid.api.ManagerIf.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 15:00
 */
interface ViewerPasswordChangeIf<T extends View,B> extends ViewerIf<T,B> {
    String[] getPswdDataFromView();
    boolean checkLoginData();
}
