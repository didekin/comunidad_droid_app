package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekinaar.R;
import com.didekinaar.comunidad.ComuSearchAc;
import com.didekinaar.usuario.LoginAc;
import com.didekinaar.usuario.UserDataAc;
import com.didekinaar.utils.UIutils;

import java.util.Objects;

import static com.didekinaar.utils.UIutils.isRegisteredUser;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:27
 */

public enum UiAarAction implements UiExceptionActionIf {

    /*GENERIC_PROV {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            intent.setClass(activity, IncidSearchAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            UIutils.makeToast(activity, R.string.exception_generic_message);
            UiExceptionActionUtil.finishActivity(activity, intent);
        }
    },*/
    GENERIC_USER {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            intent.setClass(activity, ComuSearchAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            UIutils.makeToast(activity, R.string.exception_generic_message);
            UiExceptionActionUtil.finishActivity(activity, intent);
        }
    },
    LOGIN {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            UIutils.makeToast(activity, R.string.user_without_signedUp);
            doCommonLogin(activity, intent);
        }
    },
    SEARCH_COMU {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            intent.setClass(activity, ComuSearchAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            UIutils.makeToast(activity, R.string.comunidad_not_found_message);
            UiExceptionActionUtil.finishActivity(activity, intent);
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
            Objects.equals(isRegisteredUser(activity), true);
            intent.setClass(activity, UserDataAc.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            UIutils.makeToast(activity, R.string.user_data_not_modified_msg);
            UiExceptionActionUtil.finishActivity(activity, intent);
        }
    },
    ;

//    ====================== STATIC HELPER METHODS ========================

    public static void doCommonLogin(Activity activity, Intent intent)
    {
        intent.setClass(activity, LoginAc.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        UiExceptionActionUtil.finishActivity(activity, intent);
    }
}
