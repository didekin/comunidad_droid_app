package com.didekindroid.incidencia.activity.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.didekindroid.incidencia.activity.IncidCommentRegAc;
import com.didekindroid.incidencia.activity.IncidCommentSeeAc;
import com.didekindroid.incidencia.activity.IncidRegAc;
import com.didekindroid.incidencia.activity.IncidResolucionRegEditSeeAc;
import com.didekindroid.incidencia.activity.IncidSeeClosedByComuAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 15:17
 */
public enum IncidenciaMenu {

    INCID_CLOSED_BY_COMU_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_closed_by_comu.doMenuItem()");
            Intent intent = new Intent(activity, IncidSeeClosedByComuAc.class);
            activity.startActivity(intent);
        }
    },

    INCID_COMMENT_REG_AC{
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_comment_reg.doMenuItem()");
            Intent intent = activity.getIntent();
            intent.setClass(activity, IncidCommentRegAc.class);
            activity.startActivity(intent);
        }
    },

    INCID_COMMENTS_SEE_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_comments_see.doMenuItem()");
            Intent intent = activity.getIntent();
            intent.setClass(activity, IncidCommentSeeAc.class);
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

    INCID_RESOLUCION_REG_EDIT_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_resolucion_ac.doMenuItem()");
            Intent intent = activity.getIntent();
            intent.setClass(activity, IncidResolucionRegEditSeeAc.class);
            activity.startActivity(intent);
        }
    },

    INCID_SEE_BY_COMU_AC {
        @Override
        public void doMenuItem(Activity activity)
        {
            Log.d(TAG, "incid_see_by_comu.doMenuItem()");
            Intent intent = new Intent(activity, IncidSeeOpenByComuAc.class);
            activity.startActivity(intent);
        }
    },

    ;

    private static final String TAG =  IncidenciaMenu.class.getCanonicalName();

    public abstract void doMenuItem(Activity activity);
}
