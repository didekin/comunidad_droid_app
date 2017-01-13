package com.didekindroid.exception;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.activity.IncidEditAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.login.LoginAppAc;
import com.didekindroid.usuario.userdata.UserDataAppAc;

import java.util.HashMap;
import java.util.Map;

import static com.didekin.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekin.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekin.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekin.comunidad.ComunidadExceptionMsg.COMUNIDAD_WRONG_INIT;
import static com.didekin.comunidad.ComunidadExceptionMsg.SUFIJO_NUM_IN_COMUNIDAD_NULL;
import static com.didekin.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekin.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekin.http.GenericExceptionMsg.NOT_FOUND;
import static com.didekin.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekin.http.GenericExceptionMsg.UNAUTHORIZED;
import static com.didekin.http.GenericExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.AVANCE_WRONG_INIT;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekin.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_WRONG_INIT;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_NOT_EQUAL_ABLE;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_NOT_HASHABLE;
import static com.didekin.usuario.UsuarioExceptionMsg.USER_WRONG_INIT;
import static com.didekin.usuariocomunidad.UsuarioComunidadExceptionMsg.ROLES_NOT_FOUND;
import static com.didekin.usuariocomunidad.UsuarioComunidadExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekin.usuariocomunidad.UsuarioComunidadExceptionMsg.USER_COMU_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public final class UiExceptionDealer implements UiExceptionIf.UiExceptionDealerIf {

    private static final UiExceptionIf.ActionForUiExceptionIf GENERIC_APP_ACC = new ActionForUiException(ComuSearchAc.class, R.string.exception_generic_app_message);
    private static final UiExceptionIf.ActionForUiExceptionIf LOGIN_ACC = new ActionForUiException(LoginAppAc.class, R.string.user_without_signedUp);
    private static final UiExceptionIf.ActionForUiExceptionIf SEARCH_COMU_ACC = new ActionForUiException(ComuSearchAc.class, R.string.comunidad_not_found_message);
    private static final UiExceptionIf.ActionForUiExceptionIf INCID_SEE_BY_COMU_ACC = new ActionForUiException(IncidSeeOpenByComuAc.class, R.string.incidencia_wrong_init);

    private static final Map<String, UiExceptionIf.ActionForUiExceptionIf> messageToAction = new HashMap<>();

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
        messageToAction.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), new ActionForUiException(LoginAppAc.class, R.string.user_without_powers));
        messageToAction.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(), new ActionForUiException(IncidRegAc.class, R.string.incidencia_not_registered));
        messageToAction.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(NOT_FOUND.getHttpMessage(), GENERIC_APP_ACC);
        messageToAction.put(RESOLUCION_DUPLICATE.getHttpMessage(), new ActionForUiException(IncidEditAc.class, R.string.resolucion_duplicada));
        messageToAction.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        messageToAction.put(ROLES_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC_APP_ACC);
//        messageToAction.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);  // TODO: qué hago con el borrado en servidor de token inservibles.
        messageToAction.put(TOKEN_NULL.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(UNAUTHORIZED.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        messageToAction.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), new ActionForUiException(UserDataAppAc.class, R.string.user_data_not_modified_msg));
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
    public UiExceptionIf.ActionForUiExceptionIf getActionForException(UiException uiException){
        return messageToAction.get(uiException.getErrorBean().getMessage());
    }
}
