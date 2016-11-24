package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.ROLES_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.SUFIJO_NUM_IN_COMUNIDAD_NULL;
import static com.didekin.common.exception.DidekinExceptionMsg.TOKEN_NOT_DELETED;
import static com.didekin.common.exception.DidekinExceptionMsg.TOKEN_NULL;
import static com.didekin.common.exception.DidekinExceptionMsg.UNAUTHORIZED;
import static com.didekin.common.exception.DidekinExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekin.common.exception.DidekinExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_COMU_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_EQUAL_ABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_WRONG_INIT;
import static com.didekinaar.exception.UiAarAction.GENERIC_USER;
import static com.didekinaar.exception.UiAarAction.LOGIN;
import static com.didekinaar.exception.UiAarAction.SEARCH_COMU;
import static com.didekinaar.exception.UiAarAction.TOKEN_TO_ERASE;
import static com.didekinaar.exception.UiAarAction.USER_DATA_AC;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:41
 */

public class UiAarException extends UiCmmExceptionAbs {


    public UiAarException(ErrorBean errorBean)
    {
        super(errorBean);
    }

    @Override
    public void processMe(Activity activity, Intent intent)
    {
        Timber.d("processMe(): %s %s%n", activity.getComponentName().getClassName(), errorBean.getMessage());
        messageToAarAction.get(errorBean.getMessage()).doAction(activity, intent);
    }

    private static final Map<String, UiExceptionActionIf> messageToAarAction = new HashMap<>();

    static {
//        messageToAarAction.put(NOT_FOUND.getHttpMessage(), GENERIC_PROV);
        messageToAarAction.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), GENERIC_USER);
        messageToAarAction.put(BAD_REQUEST.getHttpMessage(), LOGIN);
        messageToAarAction.put(COMUNIDAD_DUPLICATE.getHttpMessage(), SEARCH_COMU);
        messageToAarAction.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), SEARCH_COMU);
        messageToAarAction.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), SEARCH_COMU);
        messageToAarAction.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), SEARCH_COMU);
        messageToAarAction.put(GENERIC_INTERNAL_ERROR.getHttpMessage(), GENERIC_USER);
        messageToAarAction.put(NOT_FOUND.getHttpMessage(), GENERIC_USER);
        messageToAarAction.put(ROLES_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAarAction.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC_USER);
        messageToAarAction.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);
        messageToAarAction.put(TOKEN_NULL.getHttpMessage(), LOGIN);
        messageToAarAction.put(UNAUTHORIZED.getHttpMessage(), LOGIN);
        messageToAarAction.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), USER_DATA_AC);
        messageToAarAction.put(USER_NOT_COMPARABLE.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_NAME_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_NAME_DUPLICATE.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_NOT_HASHABLE.getHttpMessage(), LOGIN);
        messageToAarAction.put(USER_WRONG_INIT.getHttpMessage(), LOGIN);
        messageToAarAction.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN);
    }
}
