package com.didekindroid.common.activity;

/**
 * User: pedro@didekin
 * Date: 08/10/15
 * Time: 11:08
 */

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.IncidResolucionEditAc;
import com.didekindroid.incidencia.activity.IncidSeeByComuAc;
import com.didekindroid.usuario.activity.ComuSearchAc;
import com.didekindroid.usuario.activity.LoginAc;
import com.didekindroid.usuario.activity.UserDataAc;

import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static com.didekin.common.exception.DidekinExceptionMsg.BAD_REQUEST;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_COMMENT_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_WRONG_INIT;
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
import static com.didekin.common.exception.DidekinExceptionMsg.USER_COMU_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_DUPLICATE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_COMPARABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_EQUAL_ABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NOT_HASHABLE;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_WRONG_INIT;
import static com.didekindroid.common.activity.UiException.UiAction.GENERIC;
import static com.didekindroid.common.activity.UiException.UiAction.INCID_SEE_BY_COMU;
import static com.didekindroid.common.activity.UiException.UiAction.LOGIN;
import static com.didekindroid.common.activity.UiException.UiAction.LOGIN_INCID;
import static com.didekindroid.common.activity.UiException.UiAction.RESOLUCION_DUP;
import static com.didekindroid.common.activity.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.common.activity.UiException.UiAction.TOKEN_TO_ERASE;
import static com.didekindroid.common.activity.UiException.UiAction.USER_DATA_AC;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Exceptions to be dealt with in the user interface
 */
public class UiException extends Exception {

    private static final String TAG = UiException.class.getCanonicalName();

    private final InServiceException inServiceException;

    public UiException(InServiceException inServiceException)
    {
        this.inServiceException = inServiceException;
    }

    public void processMe(Activity activity, Intent intent)
    {
        Log.e(TAG, "processMe(): " + activity.getComponentName().getClassName() + " " + inServiceException.getHttpMessage());
        checkArgument(intent != null);
        messageToAction.get(inServiceException.getHttpMessage()).doAction(activity, intent);
    }

    public InServiceException getInServiceException()
    {
        return inServiceException;
    }

    // =============================== INNER CLASSES =============================

    public enum UiAction {

        GENERIC {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                LOGIN.doAction(activity, intent);
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
                makeToast(activity, R.string.incidencia_wrong_init, LENGTH_SHORT);
                intent.setClass(activity, IncidSeeByComuAc.class);
                activity.startActivity(intent);
                activity.finish();
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
                makeToast(activity, R.string.resolucion_duplicada, LENGTH_SHORT);
                intent.setClass(activity, IncidResolucionEditAc.class);
                activity.startActivity(intent);
                activity.finish();
            }
        },
        SEARCH_COMU {
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                makeToast(activity, R.string.comunidad_not_found_message, LENGTH_SHORT);
                intent.setClass(activity, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
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
            /**
             * Preconditions:
             * 1. The user is registered.
             * 2. There is a token in local cache.
             */
            @Override
            public void doAction(Activity activity, Intent intent)
            {
                makeToast(activity, R.string.user_data_not_modified_msg, LENGTH_SHORT);
                intent.setClass(activity, UserDataAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        };

        public abstract void doAction(Activity activity, Intent intent);

        // ......................... HELPERS ...............................

        private static void doCommonLogin(Activity activity, Intent intent)
        {
            intent.setClass(activity, LoginAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    // ...........................................................................................

    static final Map<String, UiAction> messageToAction = new HashMap<>();

    static {
        messageToAction.put(BAD_REQUEST.getHttpMessage(), LOGIN);
        messageToAction.put(COMUNIDAD_NOT_COMPARABLE.getHttpMessage(), GENERIC);
        messageToAction.put(COMUNIDAD_DUPLICATE.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(COMUNIDAD_NOT_FOUND.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(COMUNIDAD_NOT_HASHABLE.getHttpMessage(), GENERIC);
        messageToAction.put(COMUNIDAD_WRONG_INIT.getHttpMessage(), SEARCH_COMU);
        messageToAction.put(INCIDENCIA_COMMENT_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_NOT_FOUND.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(INCIDENCIA_USER_WRONG_INIT.getHttpMessage(), LOGIN_INCID);
        messageToAction.put(INCIDENCIA_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(NOT_FOUND.getHttpMessage(), GENERIC);
        messageToAction.put(RESOLUCION_DUPLICATE.getHttpMessage(), RESOLUCION_DUP);
        messageToAction.put(RESOLUCION_WRONG_INIT.getHttpMessage(), INCID_SEE_BY_COMU);
        messageToAction.put(ROLES_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(SUFIJO_NUM_IN_COMUNIDAD_NULL.getHttpMessage(), GENERIC);
        messageToAction.put(TOKEN_NOT_DELETED.getHttpMessage(), TOKEN_TO_ERASE);
        messageToAction.put(TOKEN_NULL.getHttpMessage(), LOGIN);
        messageToAction.put(UNAUTHORIZED.getHttpMessage(), LOGIN);
        messageToAction.put(UNAUTHORIZED_TX_TO_USER.getHttpMessage(), LOGIN);
        messageToAction.put(USER_COMU_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(USER_COMU_WRONG_INIT.getHttpMessage(), LOGIN);
        messageToAction.put(USER_DATA_NOT_MODIFIED.getHttpMessage(), USER_DATA_AC);
        messageToAction.put(USER_NOT_COMPARABLE.getHttpMessage(), GENERIC);
        messageToAction.put(USER_NOT_EQUAL_ABLE.getHttpMessage(), GENERIC);
        messageToAction.put(USER_NAME_NOT_FOUND.getHttpMessage(), LOGIN);
        messageToAction.put(USER_NAME_DUPLICATE.getHttpMessage(), GENERIC);
        messageToAction.put(USER_NOT_HASHABLE.getHttpMessage(), GENERIC);
        messageToAction.put(USER_WRONG_INIT.getHttpMessage(), LOGIN);
        messageToAction.put(USERCOMU_WRONG_INIT.getHttpMessage(), LOGIN);
    }
}
