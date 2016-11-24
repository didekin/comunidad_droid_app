package com.didekindroid.incidencia.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekinaar.exception.UiAarAction;
import com.didekinaar.exception.UiExceptionActionIf;
import com.didekinaar.exception.UiExceptionActionUtil;
import com.didekinaar.utils.UIutils;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.utils.IncidBundleKey;
import com.didekindroid.incidencia.activity.IncidEditAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;

import java.util.Objects;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:06
 */
enum UiAppAction implements UiExceptionActionIf {

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
            UIutils.makeToast(activity, R.string.incidencia_not_registered);
            UiExceptionActionUtil.finishActivity(activity, intent);
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
            UIutils.makeToast(activity, R.string.incidencia_wrong_init);
            UiExceptionActionUtil.finishActivity(activity, intent);
        }
    },
    LOGIN_INCID {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            UIutils.makeToast(activity, R.string.user_without_powers);
            UiAarAction.doCommonLogin(activity, intent);
        }
    },
    RESOLUCION_DUP {
        @Override
        public void doAction(Activity activity, Intent intent)
        {
            IncidImportancia incidImportancia = (IncidImportancia) intent.getSerializableExtra(IncidBundleKey.INCID_IMPORTANCIA_OBJECT.key);
            Objects.equals(incidImportancia.getUserComu().hasAdministradorAuthority(), true);
            intent.setClass(activity, IncidEditAc.class);
            activity.startActivity(intent);
            UIutils.makeToast(activity, R.string.resolucion_duplicada);
            UiExceptionActionUtil.finishActivity(activity, intent);
        }
    },
    ;
}
