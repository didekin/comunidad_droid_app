package com.didekindroid.exception;

import android.app.Activity;
import android.os.Bundle;

import com.didekindroid.R;
import com.didekindroid.api.router.ActionForUiExceptionIf;
import com.didekindroid.api.router.UiExceptionRouterIf;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.userdata.UserDataAc;

import java.util.HashMap;
import java.util.Map;

import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_comunidad_search_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_incidReg_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_incid_list_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_login_noPowers_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_login_noUser_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_resolucionDup;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_userData_noModified_action;
import static com.didekindroid.exception.UiExceptionRouter.ActionsForRouter.show_userData_wrongMail_action;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.GenericExceptionMsg.UNAUTHORIZED;
import static com.didekinlib.http.GenericExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_WRONG_INIT;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.SUFIJO_NUM_IN_COMUNIDAD_NULL;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.AVANCE_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCIDENCIA_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.INCID_IMPORTANCIA_WRONG_INIT;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.model.incidencia.dominio.IncidenciaExceptionMsg.RESOLUCION_WRONG_INIT;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NOT_EQUAL_ABLE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NOT_HASHABLE;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_WRONG_INIT;
import static com.didekinlib.model.usuariocomunidad.UsuarioComunidadExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekinlib.model.usuariocomunidad.UsuarioComunidadExceptionMsg.USER_COMU_NOT_FOUND;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public final class UiExceptionRouter implements UiExceptionRouterIf {

    private static final Map<String, com.didekindroid.api.router.ActionForUiExceptionIf> router = new HashMap<>();

    static {
        router.put(AVANCE_WRONG_INIT.getHttpMessage(), show_incid_list_action);
        router.put(BAD_REQUEST.getHttpMessage(), show_login_noUser_action);
        router.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), ActionsForRouter.generic_action);
        router.put(COMUNIDAD_DUPLICATE.getHttpMessage(), show_comunidad_search_action);
        router.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), show_comunidad_search_action);
        router.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), show_comunidad_search_action);
        router.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), show_comunidad_search_action);
        router.put(GENERIC_INTERNAL_ERROR.getHttpMessage(), ActionsForRouter.generic_action);
        router.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), show_incid_list_action);
        router.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), show_incid_list_action);
        router.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), show_login_noPowers_action);
        router.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), show_incid_list_action);
        router.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), show_incid_list_action);
        router.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(), show_incidReg_action);
        router.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), show_incid_list_action);
        router.put(NOT_FOUND.getHttpMessage(), ActionsForRouter.generic_action);
        router.put(PASSWORD_NOT_SENT.getHttpMessage(), show_userData_wrongMail_action);
        router.put(RESOLUCION_DUPLICATE.getHttpMessage(), show_resolucionDup);
        router.put(RESOLUCION_WRONG_INIT.getHttpMessage(), show_incid_list_action);
        router.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), ActionsForRouter.generic_action);
//        router.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);  // TODO: qué hago con el borrado en servidor de token inservibles.
        router.put(TOKEN_NULL.getHttpMessage(), show_login_noUser_action);
        router.put(UNAUTHORIZED.getHttpMessage(), show_login_noUser_action);
        router.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), show_login_noUser_action);
        router.put(USERCOMU_WRONG_INIT.getHttpMessage(), show_login_noUser_action);
        router.put(USER_COMU_NOT_FOUND.getHttpMessage(), show_login_noUser_action);
        router.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), show_userData_noModified_action);
        router.put(USER_DATA_NOT_INSERTED.getHttpMessage(), show_login_noUser_action);
        router.put(USER_NAME_DUPLICATE.getHttpMessage(), show_login_noUser_action);
        router.put(USER_NAME_NOT_FOUND.getHttpMessage(), show_login_noUser_action);
        router.put(USER_NOT_COMPARABLE.getHttpMessage(), show_login_noUser_action);
        router.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), show_login_noUser_action);
        router.put(USER_NOT_HASHABLE.getHttpMessage(), show_login_noUser_action);
        router.put(USER_WRONG_INIT.getHttpMessage(), show_login_noUser_action);
    }

    public UiExceptionRouter()
    {
    }

    @Override
    public ActionForUiExceptionIf getAction(UiException uiException)
    {
        return router.get(uiException.getErrorBean().getMessage());
    }

    public enum ActionsForRouter implements ActionForUiExceptionIf {

        generic_action(R.string.exception_generic_app_message, ComuSearchAc.class),
        show_incid_list_action(R.string.incidencia_wrong_init, IncidSeeByComuAc.class) {
            @Override
            public Bundle getExtrasForActivity()
            {
                Bundle bundle = new Bundle(1);
                bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
                return bundle;
            }
        },
        show_login_noUser_action(R.string.user_without_signedUp, LoginAc.class),
        show_login_noPowers_action(R.string.user_without_powers, LoginAc.class),
        show_comunidad_search_action(R.string.comunidad_not_found_message, ComuSearchAc.class),
        show_incidReg_action(R.string.incidencia_not_registered, IncidRegAc.class),
        show_userData_wrongMail_action(R.string.user_email_wrong, UserDataAc.class),
        show_userData_noModified_action(R.string.user_data_not_modified_msg, UserDataAc.class),
        show_resolucionDup(R.string.resolucion_duplicada, null),;

        private final int resourceIdForMsg;
        private final Class<? extends Activity> activityToGo;

        ActionsForRouter(int resourceIdForMsg, Class<? extends Activity> activityToGo)
        {
            this.resourceIdForMsg = resourceIdForMsg;
            this.activityToGo = activityToGo;
        }

        @Override
        public Class<? extends Activity> getActivityToGo()
        {
            return activityToGo;
        }

        @Override
        public Bundle getExtrasForActivity()
        {
            return new Bundle(0);
        }

        @Override
        public int getToastResourceId()
        {
            return resourceIdForMsg;
        }
    }
}
