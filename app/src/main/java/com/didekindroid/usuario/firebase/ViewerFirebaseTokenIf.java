package com.didekindroid.usuario.firebase;

import android.view.View;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 15:06
 */

public interface ViewerFirebaseTokenIf<B> extends ManagerIf.ViewerIf {

    void setController(ControllerFirebaseTokenIf controllerIn);

    void checkGcmTokenAsync();

}
