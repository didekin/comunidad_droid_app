package com.didekindroid.util;

import android.app.Activity;
import android.content.Intent;
import android.util.ArrayMap;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuDataAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.activity.IncidCommentRegAc;
import com.didekindroid.incidencia.activity.IncidCommentSeeAc;
import com.didekindroid.incidencia.activity.IncidResolucionRegEditSeeAc;
import com.didekindroid.incidencia.activity.IncidSeeClosedByComuAc;
import com.didekindroid.incidencia.activity.IncidSeeOpenByComuAc;
import com.didekindroid.incidencia.activity.incidreg.IncidRegAc;
import com.didekindroid.usuario.delete.DeleteMeAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.password.PasswordChangeAc;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekindroid.usuariocomunidad.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.RegComuAndUserComuAc;
import com.didekindroid.usuariocomunidad.SeeUserComuByComuAc;
import com.didekindroid.usuariocomunidad.SeeUserComuByUserAc;

import java.util.Map;

import static android.support.v4.app.NavUtils.getParentActivityIntent;
import static android.support.v4.app.NavUtils.navigateUpTo;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

/**
 * User: pedro@didekin
 * Date: 29/12/16
 * Time: 17:21
 */

public final class MenuRouter {

    public static final Map<Integer, Class<? extends Activity>> routerMap = new ArrayMap<>();
    private static final Map<Integer, Class<? extends Activity>> noRegisteredRouterMap = new ArrayMap<>();

    static {
        // INCIDENCIAS.
        routerMap.put(R.id.incid_comment_reg_ac_mn, IncidCommentRegAc.class);
        routerMap.put(R.id.incid_comments_see_ac_mn, IncidCommentSeeAc.class);
        routerMap.put(R.id.incid_reg_ac_mn, IncidRegAc.class);
        routerMap.put(R.id.incid_resolucion_reg_ac_mn, IncidResolucionRegEditSeeAc.class);
        routerMap.put(R.id.incid_see_closed_by_comu_ac_mn, IncidSeeClosedByComuAc.class);
        routerMap.put(R.id.incid_see_open_by_comu_ac_mn, IncidSeeOpenByComuAc.class);
        // USUARIO REGISTRADO.
        routerMap.put(R.id.comu_data_ac_mn, ComuDataAc.class);
        routerMap.put(R.id.comu_search_ac_mn, ComuSearchAc.class);
        routerMap.put(R.id.delete_me_ac_mn, DeleteMeAc.class);
        routerMap.put(R.id.login_ac_mn, LoginAc.class);
        routerMap.put(R.id.password_change_ac_mn, PasswordChangeAc.class);
        routerMap.put(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserComuAc.class);
        routerMap.put(R.id.see_usercomu_by_comu_ac_mn, SeeUserComuByComuAc.class);
        routerMap.put(R.id.see_usercomu_by_user_ac_mn, SeeUserComuByUserAc.class);
        routerMap.put(R.id.user_data_ac_mn, UserDataAc.class);

        /* USUARIO NO REGISTRADO.*/
        noRegisteredRouterMap.put(R.id.reg_nueva_comunidad_ac_mn, RegComuAndUserAndUserComuAc.class);
        noRegisteredRouterMap.put(android.R.id.home, ComuSearchAc.class);
    }

    public static Class<? extends Activity> getRegisterDependentClass(int resourceId)
    {   // TODO: test both cases.
        if (TKhandler.isRegisteredUser()) {
            return routerMap.get(resourceId);
        } else {
            return noRegisteredRouterMap.get(resourceId);
        }
    }

    public static void doUpMenu(Activity activity)
    {
        Intent intent = getParentActivityIntent(activity);
        finishNavigateUp(activity, intent);
    }

    public static void doUpMenuWithIntent(Activity activity, Intent parentIntent)
    {
        finishNavigateUp(activity, parentIntent);
    }

    private static void finishNavigateUp(Activity activity, Intent intent)
    {
        // We need both flags to reuse the intent of the parent activity.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        navigateUpTo(activity, intent);
    }
}
