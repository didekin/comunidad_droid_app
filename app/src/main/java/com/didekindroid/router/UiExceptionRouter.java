package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.RouterTo;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekinlib.http.exception.ExceptionMsgIf;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
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

public enum UiExceptionRouter implements RouterTo {

    generic(
            of(GENERIC_INTERNAL_ERROR, NOT_FOUND),
            R.string.exception_generic_app_message,
            ComuSearchAc.class) {
        @Override
        public void initActivity(@NonNull Activity activity)
        {
            Timber.d("initActivity()");
            makeToast(activity, getResourceIdForToast());
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    .setAction(Intent.ACTION_MAIN)
                    .setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
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
            Bundle bundle = new Bundle(1);
            bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
            initActivity(activity, bundle);
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
            null),
    show_userData_wrongMail(of(PASSWORD_NOT_SENT),
            R.string.user_email_wrong,
            UserDataAc.class),;

    // ==========================  Static members ============================

    private static final Map<String, UiExceptionRouter> exceptionMsgMap = new HashMap<>(values().length * 3);

    static {
        for (UiExceptionRouter router : values()) {
            for (ExceptionMsgIf message : router.exceptionMsgSet) {
                exceptionMsgMap.put(message.getHttpMessage(), router);
            }
        }
    }

    public static UiExceptionRouter getExceptionRouter(@NonNull String httpMsg)
    {
        Timber.d("getExceptionRouter()");
        UiExceptionRouter router = exceptionMsgMap.get(httpMsg);
        return router == null ? generic : router;
    }

    // ==========================  Instance members ============================

    private final Class<? extends Activity> acToGo;
    private final Set<? extends ExceptionMsgIf> exceptionMsgSet;
    private final int resourceIdForToast;

    UiExceptionRouter(EnumSet<? extends ExceptionMsgIf> httpMessages, int resourceString, Class<? extends Activity> acClassToGo)
    {
        exceptionMsgSet = unmodifiableSet(httpMessages);
        resourceIdForToast = resourceString;
        acToGo = acClassToGo;
    }

    @Override
    public void initActivity(@NonNull Activity activity, @Nullable Bundle bundle)
    {
        Timber.d("initActivity()");
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
