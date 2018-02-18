package com.didekindroid.lib_one.usuario.notification;


import com.didekindroid.lib_one.api.ControllerIf;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 17/01/17
 * Time: 14:01
 */

public interface CtrlerNotifyTokenIf extends ControllerIf {

    boolean checkGcmTokenAsync(DisposableSingleObserver<Integer> observer);

    boolean checkGcmTokenSync(DisposableSingleObserver<Integer> observer);

}
