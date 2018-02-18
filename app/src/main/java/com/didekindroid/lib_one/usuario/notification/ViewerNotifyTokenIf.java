package com.didekindroid.lib_one.usuario.notification;

import android.view.View;

import com.didekindroid.lib_one.api.ViewerIf;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 15:06
 */

public interface ViewerNotifyTokenIf<T extends View> extends
        ViewerIf<T, CtrlerNotifyTokenIf> {

    void checkGcmTokenAsync();
}
