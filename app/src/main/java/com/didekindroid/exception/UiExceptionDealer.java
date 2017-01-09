package com.didekindroid.exception;

import com.didekinaar.exception.ActionForUiExceptionIf;
import com.didekinaar.exception.UiException;
import com.didekinaar.exception.UiExceptionDealerIf;
import com.didekinaar.usuario.userdata.UserDataAc;
import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.activity.IncidEditAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.login.LoginAppAc;
import com.didekindroid.usuario.userdata.UserDataAppAc;

import java.util.HashMap;
import java.util.Map;

import static com.didekin.common.exception.DidekinExceptionMsg.AVANCE_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.RESOLUCION_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.ROLES_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.SUFIJO_NUM_IN_COMUNIDAD_NULL;
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

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public final class UiExceptionDealer implements UiExceptionDealerIf {

    private static final ActionForUiExceptionIf GENERIC_APP_ACC = new UiException.ActionForUiException(ComuSearchAc.class, R.string.exception_generic_app_message);
    private static final ActionForUiExceptionIf LOGIN_ACC = new UiException.ActionForUiException(LoginAppAc.class, R.string.user_without_signedUp);
    private static final ActionForUiExceptionIf SEARCH_COMU_ACC = new UiException.ActionForUiException(ComuSearchAc.class, R.string.comunidad_not_found_message);
    private static final ActionForUiExceptionIf INCID_SEE_BY_COMU_ACC = new UiException.ActionForUiException(IncidSeeOpenByComuAc.class, R.string.incidencia_wrong_init);

    private static final Map<String, ActionForUiExceptionIf> messageToAction = new HashMap<>();

    static {
        messageToAction.put(AVANCE_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(BAD_REQUEST.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), GENERIC_APP_ACC);
        messageToAction.put(COMUNIDAD_DUPLICATE.getHttpMessage(), SEARCH_COMU_ACC);
        messageToAction.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), SEARCH_COMU_ACC);
        messageToAction.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), SEARCH_COMU_ACC);
        messageToAction.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), SEARCH_COMU_ACC);
        messageToAction.put(GENERIC_INTERNAL_ERROR.getHttpMessage(), GENERIC_APP_ACC);
        messageToAction.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), new UiException.ActionForUiException(LoginAppAc.class, R.string.user_without_powers));
        messageToAction.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(), new UiException.ActionForUiException(IncidRegAc.class, R.string.incidencia_not_registered));
        messageToAction.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(NOT_FOUND.getHttpMessage(), GENERIC_APP_ACC);
        messageToAction.put(RESOLUCION_DUPLICATE.getHttpMessage(), new UiException.ActionForUiException(IncidEditAc.class, R.string.resolucion_duplicada));
        messageToAction.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(ROLES_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC_APP_ACC);
//        messageToAction.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);  // TODO: qué hago con el borrado en servidor de token inservibles.
        messageToAction.put(TOKEN_NULL.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(UNAUTHORIZED.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), new UiException.ActionForUiException(UserDataAppAc.class, R.string.user_data_not_modified_msg));
        messageToAction.put(USER_NOT_COMPARABLE.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_NAME_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_NAME_DUPLICATE.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_NOT_HASHABLE.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_WRONG_INIT.getHttpMessage(), LOGIN_ACC);
    }

    public UiExceptionDealer()
    {
    }

    @Override
    public ActionForUiExceptionIf getActionForException(UiException uiException){
        return messageToAction.get(uiException.getErrorBean().getMessage());
    }
}
