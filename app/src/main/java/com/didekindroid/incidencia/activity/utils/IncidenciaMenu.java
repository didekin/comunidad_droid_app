package com.didekindroid.incidencia.activity.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.didekindroid.incidencia.activity.IncidClosedByUserAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.usuario.activity.ComuSearchAc;

import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:17
 */
public enum IncidenciaMenu {

    INCID_CLOSED_BY_USER_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_reg_ac.doMenuItem()");
            Intent intent = new Intent(activity, IncidClosedByUserAc.class);
            activity.startActivity(intent);
        }
    },

    INCID_REG_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_reg_ac.doMenuItem()");
            Intent intent = new Intent(activity, IncidRegAc.class);
            activity.startActivity(intent);
        }
    },

    ;

    private static final String TAG =  IncidenciaMenu.class.getCanonicalName();

    public abstract void doMenuItem(Activity activity);
}
