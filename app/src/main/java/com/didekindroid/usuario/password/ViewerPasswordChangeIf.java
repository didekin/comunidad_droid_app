package com.didekindroid.usuario.password;

import android.view.View;

import com.didekindroid.api.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 15:00
 */
interface ViewerPasswordChangeIf extends ViewerIf<View,CtrlerPasswordChangeIf> {
    boolean checkLoginData();
}
