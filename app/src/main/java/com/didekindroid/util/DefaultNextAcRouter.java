package com.didekindroid.util;

import android.app.Activity;
import android.util.ArrayMap;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ActivityNextMock;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.core.reg.IncidRegAc;
import com.didekindroid.incidencia.list.IncidSeeOpenByComuAc;
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

public class DefaultNextAcRouter {

    public static final Map<Class<? extends Activity>, Class<? extends Activity>> routerMap = new ArrayMap<>();

    static {
        routerMap.put(DeleteMeAc.class, ComuSearchAc.class);
        routerMap.put(IncidRegAc.class, IncidSeeOpenByComuAc.class);
        routerMap.put(LoginAc.class, ComuSearchAc.class);
        routerMap.put(PasswordChangeAc.class, UserDataAc.class);
        routerMap.put(UserDataAc.class, SeeUserComuByUserAc.class);

        // Tests.
        routerMap.put(ActivityMock.class, ActivityNextMock.class);
    }
}
