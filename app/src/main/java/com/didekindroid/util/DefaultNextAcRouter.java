package com.didekindroid.util;

import android.app.Activity;
import android.util.ArrayMap;

import com.didekindroid.comunidad.ComuSearchAc;
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
        routerMap.put(LoginAc.class, ComuSearchAc.class);
        routerMap.put(PasswordChangeAc.class, UserDataAc.class);
        routerMap.put(UserDataAc.class, SeeUserComuByUserAc.class);
    }
}
