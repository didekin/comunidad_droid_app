package com.didekindroid.router;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.router.UiExceptionActionIf;
import com.didekindroid.lib_one.usuario.LoginAc;
import com.didekinlib.http.exception.ExceptionMsgIf;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.DidekinApp.defaultAc;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.usuario.router.UserUiExceptionAction.userExceptionMsgMap;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekinlib.http.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.http.exception.GenericExceptionMsg.NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_NOT_REGISTERED;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.INCID_IMPORTANCIA_NOT_FOUND;
import static com.didekinlib.http.incidencia.IncidenciaExceptionMsg.RESOLUCION_DUPLICATE;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;

/**
 * User: pedro@didekin
 * Date: 09/01/17
 * Time: 13:13
 */

public enum DidekinUiExceptionAction implements UiExceptionActionIf {

    generic(
            of(
                    GENERIC_INTERNAL_ERROR,
                    NOT_FOUND),
            com.didekindroid.lib_one.R.string.exception_generic_message,
            defaultAc) {
        @Override
        public void handleExceptionInUi(@NonNull Activity activity)
        {
            handleExceptionInUi(activity, null, FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
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
            of(
                    INCIDENCIA_NOT_FOUND,
                    INCID_IMPORTANCIA_NOT_FOUND),
            R.string.incidencia_wrong_init,
            IncidSeeByComuAc.class) {
        @Override
        public void handleExceptionInUi(@NonNull Activity activity)
        {
            handleExceptionInUi(activity, null);
        }

        @Override
        public void handleExceptionInUi(@NonNull Activity activity, @Nullable Bundle bundleIn)
        {
            Bundle bundle = bundleIn != null ? bundleIn : new Bundle(1);
            bundle.putBoolean(INCID_CLOSED_LIST_FLAG.key, false);
            handleExceptionInUi(activity, bundle, FLAG_ACTIVITY_NEW_TASK);
        }
    },
    show_login_noPowers(
            of(INCIDENCIA_USER_WRONG_INIT),
            R.string.user_without_powers,
            LoginAc.class),
    show_resolucionDup(
            of(RESOLUCION_DUPLICATE),
            R.string.resolucion_duplicada,
            IncidSeeByComuAc.class) {
        @Override
        public void handleExceptionInUi(@NonNull Activity activity)
        {
            handleExceptionInUi(activity, INCID_CLOSED_LIST_FLAG.getBundleForKey(false), FLAG_ACTIVITY_NEW_TASK);
        }
    },;

    // ==========================  Static members ============================

    public static final Map<String, UiExceptionActionIf> didekinExcpMsgMap =
            new HashMap<>((values().length * 3) + userExceptionMsgMap.size());

    static {
        didekinExcpMsgMap.putAll(userExceptionMsgMap);
        for (DidekinUiExceptionAction action : values()) {
            for (ExceptionMsgIf message : action.exceptionMsgSet) {
                didekinExcpMsgMap.put(message.getHttpMessage(), action);
            }
        }
    }

    /* ==========================  Instance members ============================*/

    private final int resourceIdForToast;
    private final Class<? extends Activity> acToGo;
    private final Set<? extends ExceptionMsgIf> exceptionMsgSet;

    DidekinUiExceptionAction(EnumSet<? extends ExceptionMsgIf> httpMessages, int resourceString, Class<? extends Activity> acClassToGo)
    {
        exceptionMsgSet = unmodifiableSet(httpMessages);
        resourceIdForToast = resourceString;
        acToGo = acClassToGo;
    }

    @Override
    public void handleExceptionInUi(@NonNull Activity activity)
    {
        handleExceptionInUi(activity, null, FLAG_ACTIVITY_NEW_TASK);
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
