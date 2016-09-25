package com.didekindroid.common.activity;

/**
 * User: pedro@didekin
 * Date: 08/10/15
 * Time: 11:08
 */

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidEditAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.activity.ComuSearchAc;
import com.didekindroid.usuario.activity.LoginAc;
import com.didekindroid.usuario.activity.UserDataAc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

import static android.widget.Toast.LENGTH_SHORT;
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
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.UiException.UiAction.GENERIC;
import static com.didekindroid.common.activity.UiException.UiAction.INCID_REG;
import static com.didekindroid.common.activity.UiException.UiAction.INCID_SEE_BY_COMU;
import static com.didekindroid.common.activity.UiException.UiAction.LOGIN;
import static com.didekindroid.common.activity.UiException.UiAction.LOGIN_INCID;
import static com.didekindroid.common.activity.UiException.UiAction.RESOLUCION_DUP;
import static com.didekindroid.common.activity.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.common.activity.UiException.UiAction.TOKEN_TO_ERASE;
import static com.didekindroid.common.activity.UiException.UiAction.USER_DATA_AC;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.common.utils.UIutils.makeToast;

/**
 * Exceptions to be dealt with in the user interface
 */
public class UiException extends Exception {

    private final ErrorBean errorBean;

    public UiException(ErrorBean errorBean)
    {
        this.errorBean = errorBean;
    }

    public void processMe(Activity activity, Intent intent)
    {
        Timber.d("processMe(): %s %s%n", activity.getComponentName().getClassName(), errorBean.getMessage());
        messageToAction.get(errorBean.getMessage()).doAction(activity, intent);
    }

    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    // =============================== INNER CLASSES =============================

    enum UiAction {

        GENERIC {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                intent.setClass(activity, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                makeToast(activity, R.string.exception_generic_message, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        },
        INCID_REG {
            /**
             * Preconditions:
             * 1. The user is registered.
             * 2. There is a token in local cache.
             */
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                intent.setClass(activity, IncidRegAc.class);
                activity.startActivity(intent);
                makeToast(activity, R.string.incidencia_not_registered, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        },
        INCID_SEE_BY_COMU {
            /**
             * Preconditions:
             * 1. The user is registered.
             * 2. There is a token in local cache.
             */
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                intent.setClass(activity, IncidSeeOpenByComuAc.class);
                activity.startActivity(intent);
                makeToast(activity, R.string.incidencia_wrong_init, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        },
        LOGIN {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                makeToast(activity, R.string.user_without_signedUp, LENGTH_SHORT);
                doCommonLogin(activity, intent);
            }
        },
        LOGIN_INCID {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                makeToast(activity, R.string.user_without_powers, LENGTH_SHORT);
                doCommonLogin(activity, intent);
            }
        },
        RESOLUCION_DUP {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                IncidImportancia incidImportancia = (IncidImportancia) intent.getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
                Objects.equals(incidImportancia.getUserComu().hasAdministradorAuthority(),true);
                intent.setClass(activity, IncidEditAc.class);
                activity.startActivity(intent);
                makeToast(activity, R.string.resolucion_duplicada, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        },
        SEARCH_COMU {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                intent.setClass(activity, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                makeToast(activity, R.string.comunidad_not_found_message, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        },
        TOKEN_TO_ERASE {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
//              Problem: an invalid token may remain in server DB, if delete of the token failed.
                // TODO: Erase in server the invalid tokens.
            }
        },
        USER_DATA_AC {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                Objects.equals(isRegisteredUser(activity),true);
                intent.setClass(activity, UserDataAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                makeToast(activity, R.string.user_data_not_modified_msg, LENGTH_SHORT);
                finishActivity(activity, intent);
            }
        };

        public abstract void doAction(Activity activity, Intent intent);

        // ......................... HELPERS ...............................

        static void doCommonLogin(Activity activity, Intent intent)
        {
            intent.setClass(activity, LoginAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            finishActivity(activity, intent);
        }

        static void finishActivity(Activity activity, Intent intent)
        {
            if (!activity.getClass().getCanonicalName().equals(intent.getComponent().getClassName())) {
                activity.finish();
            }
        }
    }

    // ...........................................................................................

    private static final Map<String, UiAction> messageToAction = new HashMap<>();

    static {
        messageToAction.put(AVANCE_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(BAD_REQUEST.getHttpMessage(), LOGIN);
        messageToAction.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), GENERIC);
        messageToAction.put(COMUNIDAD_DUPLICATE.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(GENERIC_INTERNAL_ERROR.getHttpMessage(), GENERIC);
        messageToAction.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), LOGIN_INCID);
        messageToAction.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCID_IMPORTANCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_NOT_REGISTERED.getHttpMessage(), INCID_REG);
        messageToAction.put(INCID_IMPORTANCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(NOT_FOUND.getHttpMessage(), GENERIC);
        messageToAction.put(RESOLUCION_DUPLICATE.getHttpMessage(), RESOLUCION_DUP);
//        messageToAction.put(RESOLUCION_NOT_FOUND.getHttpMessage(), No debiera llegar al cliente Android);
        messageToAction.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(ROLES_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC);
        messageToAction.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);
        messageToAction.put(TOKEN_NULL.getHttpMessage(), LOGIN);
        messageToAction.put(UNAUTHORIZED.getHttpMessage(), LOGIN);
        messageToAction.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN);
        messageToAction.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), USER_DATA_AC);
        messageToAction.put(USER_NOT_COMPARABLE.getHttpMessage(), LOGIN);
        messageToAction.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), LOGIN);
        messageToAction.put(USER_NAME_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(USER_NAME_DUPLICATE.getHttpMessage(), LOGIN);
        messageToAction.put(USER_NOT_HASHABLE.getHttpMessage(), LOGIN);
        messageToAction.put(USER_WRONG_INIT.getHttpMessage(), LOGIN);
        messageToAction.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN);
    }
}
