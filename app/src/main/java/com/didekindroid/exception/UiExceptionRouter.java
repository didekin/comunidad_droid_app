package com.didekindroid.exception;

import android.content.Intent;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.exception.UiExceptionIf.UiExceptionRouterIf;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.userdata.UserDataAc;

import java.util.HashMap;
import java.util.Map;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIAS_CLOSED_LIST_FLAG;
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

    private static final UiExceptionIf.IntentForUiExceptionIf GENERIC_APP_ACC =
            new IntentForUiException(new Intent(creator.get().getContext(), ComuSearchAc.class),
                    R.string.exception_generic_app_message);
    private static final UiExceptionIf.IntentForUiExceptionIf INCID_SEE_BY_COMU_ACC =
            new IntentForUiException(
                    new Intent(creator.get().getContext(), IncidSeeByComuAc.class).putExtra(INCIDENCIAS_CLOSED_LIST_FLAG.key, false),
                    R.string.incidencia_wrong_init);
    private static final UiExceptionIf.IntentForUiExceptionIf LOGIN_ACC =
            new IntentForUiException(new Intent(creator.get().getContext(), LoginAc.class),
                    R.string.user_without_signedUp);
    private static final UiExceptionIf.IntentForUiExceptionIf SEARCH_COMU_ACC =
            new IntentForUiException(new Intent(creator.get().getContext(), ComuSearchAc.class),
                    R.string.comunidad_not_found_message);


    private static final Map<String, UiExceptionIf.IntentForUiExceptionIf> router = new HashMap<>();

    static {
        router.put(AVANCE_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(BAD_REQUEST.getHttpMessage(), LOGIN_ACC);
        router.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), GENERIC_APP_ACC);
        router.put(COMUNIDAD_DUPLICATE.getHttpMessage(), SEARCH_COMU_ACC);
        router.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), SEARCH_COMU_ACC);
        router.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), SEARCH_COMU_ACC);
        router.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), SEARCH_COMU_ACC);
        router.put(GENERIC_INTERNAL_ERROR.getHttpMessage(), GENERIC_APP_ACC);
        router.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(),
                new IntentForUiException(new Intent(creator.get().getContext(), LoginAc.class), R.string.user_without_powers));
        router.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(),
                new IntentForUiException(new Intent(creator.get().getContext(), IncidRegAc.class), R.string.incidencia_not_registered));
        router.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(NOT_FOUND.getHttpMessage(), GENERIC_APP_ACC);
        router.put(PASSWORD_NOT_SENT.getHttpMessage(),
                new IntentForUiException(new Intent(creator.get().getContext(), UserDataAc.class), R.string.user_email_wrong));
        router.put(RESOLUCION_DUPLICATE.getHttpMessage(), new IntentForUiException(R.string.resolucion_duplicada));
        router.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU_ACC);
        router.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC_APP_ACC);
//        router.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);  // TODO: qué hago con el borrado en servidor de token inservibles.
        router.put(TOKEN_NULL.getHttpMessage(), LOGIN_ACC);
        router.put(UNAUTHORIZED.getHttpMessage(), LOGIN_ACC);
        router.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN_ACC);
        router.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN_ACC);
        router.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        router.put(USER_DATA_NOT_MODIFIED.getHttpMessage(),
                new IntentForUiException(new Intent(creator.get().getContext(), UserDataAc.class), R.string.user_data_not_modified_msg));
        router.put(USER_DATA_NOT_INSERTED.getHttpMessage(), LOGIN_ACC);
        router.put(USER_NAME_DUPLICATE.getHttpMessage(), LOGIN_ACC);
        router.put(USER_NAME_NOT_FOUND.getHttpMessage(), LOGIN_ACC);
        router.put(USER_NOT_COMPARABLE.getHttpMessage(), LOGIN_ACC);
        router.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), LOGIN_ACC);
        router.put(USER_NOT_HASHABLE.getHttpMessage(), LOGIN_ACC);
        router.put(USER_WRONG_INIT.getHttpMessage(), LOGIN_ACC);
    }

    public UiExceptionRouter()
    {
    }

    @Override
    public UiExceptionIf.IntentForUiExceptionIf getActionForException(UiException uiException)
    {
        return router.get(uiException.getErrorBean().getMessage());
    }
}
