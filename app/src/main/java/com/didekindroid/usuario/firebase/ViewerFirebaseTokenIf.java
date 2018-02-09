package com.didekindroid.usuario.firebase;

import android.view.View;

import com.didekindroid.lib_one.api.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 15:06
 */

public interface ViewerFirebaseTokenIf<T extends View> extends
        ViewerIf<T, CtrlerFirebaseTokenIf> {

    void checkGcmTokenAsync();
}
