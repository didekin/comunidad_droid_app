package com.didekindroid.incidencia.activity.utils;

import android.app.Activity;
import android.util.Log;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:17
 */
public enum IncidenciaMenu {

    INCID_REG_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_reg_ac.doMenuItem()");
            activity.startActivity(activity.getIntent());
        }
    },
    ;

    private static final String TAG =  IncidenciaMenu.class.getCanonicalName();

    public abstract void doMenuItem(Activity activity);
}
