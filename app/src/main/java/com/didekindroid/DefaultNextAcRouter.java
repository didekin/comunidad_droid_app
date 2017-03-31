package com.didekindroid;

import android.app.Activity;
import android.util.ArrayMap;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.ComuSearchResultsAc;
import com.didekindroid.incidencia.core.IncidEditAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.open.IncidSeeOpenByComuAc;
import com.didekindroid.usuario.delete.DeleteMeAc;
import com.didekindroid.usuario.login.LoginAc;
import com.didekindroid.usuario.password.PasswordChangeAc;
import com.didekindroid.usuario.userdata.UserDataAc;
import com.didekindroid.usuariocomunidad.SeeUserComuByUserAc;

import java.util.Map;

/**
 * User: pedro@didekin
 * Date: 20/01/17
 * Time: 16:28
 */

public class DefaultNextAcRouter implements ActivityRouter {

    private static final Map<Class<? extends Activity>, Class<? extends Activity>> routerMap = new ArrayMap<>();
    public static final DefaultNextAcRouter acRouter = new DefaultNextAcRouter();

    static {
        routerMap.put(ComuSearchAc.class, ComuSearchResultsAc.class);
        routerMap.put(DeleteMeAc.class, ComuSearchAc.class);
        routerMap.put(IncidRegAc.class, IncidSeeOpenByComuAc.class);
        routerMap.put(IncidSeeOpenByComuAc.class, IncidEditAc.class);
        routerMap.put(LoginAc.class, ComuSearchAc.class);
        routerMap.put(PasswordChangeAc.class, UserDataAc.class);
        routerMap.put(UserDataAc.class, SeeUserComuByUserAc.class);
    }

    @Override
    public Class<? extends Activity> getNextActivity(Class<? extends Activity> previousActivity)
    {
        return routerMap.get(previousActivity);
    }
}