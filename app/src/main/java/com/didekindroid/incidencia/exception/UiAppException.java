package com.didekindroid.incidencia.exception;

/**
 * User: pedro@didekin
 * Date: 08/10/15
 * Time: 11:08
 */

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;
import com.didekinaar.exception.UiCmmExceptionAbs;
import com.didekinaar.exception.UiExceptionActionIf;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.AVANCE_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_WRONG_INIT;
import static com.didekinaar.exception.UiAarAction.LOGIN;
import static com.didekindroid.incidencia.exception.UiAppAction.INCID_REG;
import static com.didekindroid.incidencia.exception.UiAppAction.INCID_SEE_BY_COMU;
import static com.didekindroid.incidencia.exception.UiAppAction.LOGIN_INCID;
import static com.didekindroid.incidencia.exception.UiAppAction.RESOLUCION_DUP;

/**
 * Exceptions to be dealt with in the user interface
 */
public class UiAppException extends UiCmmExceptionAbs {

    public UiAppException(ErrorBean errorBean)
    {
        super(errorBean);
    }

    @Override
    public void processMe(Activity activity, Intent intent)
    {
        Timber.d("processMe(): %s %s%n", activity.getComponentName().getClassName(), errorBean.getMessage());
        messageToAction.get(errorBean.getMessage()).doAction(activity, intent);
    }

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    // ...........................................................................................

    private static final Map<String, UiExceptionActionIf> messageToAction = new HashMap<>();

    static {
        messageToAction.put(AVANCE_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(BAD_REQUEST.getHttpMessage(), LOGIN);
        messageToAction.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), LOGIN_INCID);
        messageToAction.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(), INCID_REG);
        messageToAction.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(RESOLUCION_DUPLICATE.getHttpMessage(), RESOLUCION_DUP);
        messageToAction.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
    }
}
