package com.didekindroid.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.usuario.LoginAc;
import com.didekindroid.usuario.UserDataAc;
import com.didekinlib.http.exception.ExceptionMsgIf;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.exception.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.PASSWORD_NOT_SENT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.TOKEN_NULL;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.UNAUTHORIZED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.UNAUTHORIZED_TX_TO_USER;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USERCOMU_WRONG_INIT;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_COMU_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_INSERTED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.USER_WRONG_INIT;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public enum UiExceptionAction implements RouterActionIf {

    generic(
            of(GENERIC_INTERNAL_ERROR, NOT_FOUND),
            R.string.exception_generic_app_message,
            ComuSearchAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("initActivity()");
            if (getResourceIdForToast() > 0) {
                makeToast(activity, getResourceIdForToast());
            }
            initActivity(activity, null, FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        }
    },
    show_comunidad_duplicate(
            of(COMUNIDAD_DUPLICATE),
            R.string.comunidad_duplicate,
            ComuSearchAc.class),
    show_comunidad_search(
            of(COMUNIDAD_NOT_FOUND),
            R.string.comunidad_not_found_message,
            ComuSearchAc.class),
    show_incidReg(
            of(INCIDENCIA_NOT_REGISTERED),
            R.string.incidencia_not_registered,
            IncidRegAc.class),
    show_incid_open_list(
            of(INCIDENCIA_NOT_FOUND, INCID_IMPORTANCIA_NOT_FOUND),
            R.string.incidencia_wrong_init,
            IncidSeeByComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            initActivity(activity, new Bundle());
        }

        @Override
        public void initActivity(@NonNull Activity activity, @NonNull Bundle bundleIn)
        {
            Bundle bundle = bundleIn != null ? bundleIn : new Bundle(1);
            bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
            super.initActivity(activity, bundle);
        }
    },
    show_login_noPowers(
            of(INCIDENCIA_USER_WRONG_INIT),
            R.string.user_without_powers,
            LoginAc.class),
    show_login_noUser(
            of(BAD_REQUEST, USERCOMU_WRONG_INIT, USER_COMU_NOT_FOUND, USER_DATA_NOT_INSERTED,
                    USER_NAME_DUPLICATE, USER_NAME_NOT_FOUND, USER_WRONG_INIT),
            R.string.user_without_signedUp,
            LoginAc.class),
    show_login_tokenNull(
            of(TOKEN_NULL, UNAUTHORIZED, UNAUTHORIZED_TX_TO_USER, USER_DATA_NOT_MODIFIED),
            R.string.user_with_token_null,
            LoginAc.class),
    show_resolucionDup(
            of(RESOLUCION_DUPLICATE),
            R.string.resolucion_duplicada,
            IncidSeeByComuAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            initActivity(activity, new Bundle());
        }

        @Override
        public void initActivity(@NonNull Activity activity, @NonNull Bundle bundleIn)
        {
            Bundle bundle = bundleIn != null ? bundleIn : new Bundle(1);
            bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
            super.initActivity(activity, bundle);
        }
    },
    show_userData_wrongMail(of(PASSWORD_NOT_SENT),
            R.string.user_email_wrong,
            UserDataAc.class),;

    // ==========================  Static members ============================

    static final Map<String, UiExceptionAction> exceptionMsgMap = new HashMap<>(values().length * 3);

    static {
        for (UiExceptionAction action : values()) {
            for (ExceptionMsgIf message : action.exceptionMsgSet) {
                exceptionMsgMap.put(message.getHttpMessage(), action);
            }
        }
    }

    /* ==========================  Instance members ============================*/

    private final int resourceIdForToast;
    private final Class<? extends Activity> acToGo;
    private final Set<? extends ExceptionMsgIf> exceptionMsgSet;

    UiExceptionAction(EnumSet<? extends ExceptionMsgIf> httpMessages, int resourceString, Class<? extends Activity> acClassToGo)
    {
        exceptionMsgSet = unmodifiableSet(httpMessages);
        resourceIdForToast = resourceString;
        acToGo = acClassToGo;
    }

    @Override
    public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        Timber.d("initActivity(), two parameters.");
        if (resourceIdForToast > 0) {
            makeToast(activity, resourceIdForToast);
        }
        initActivity(activity, bundle, FLAG_ACTIVITY_NEW_TASK);
    }

    public int getResourceIdForToast()
    {
        return resourceIdForToast;
    }

    public Class<? extends Activity> getAcToGo()
    {
        return acToGo;
    }
}
