package com.didekindroid;

import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.exception.UiAppExceptionTests;
import com.didekindroid.gcm.GcmBroadCastNotificationActivityTest;
import com.didekindroid.gcm.GcmRequestTest;
import com.didekindroid.incidencia.IncidSuiteFunctional;
import com.didekindroid.incidencia.IncidSuiteSupport;
import com.didekindroid.security.Oauth2DaoRemoteIf_app_Test;
import com.didekindroid.security.TokenIdentityCacher_App_Test;
import com.didekindroid.usuario.UsuarioSuite;
import com.didekindroid.usuariocomunidad.UserComuSuite;
import com.didekindroid.utils.IoHelperApp_test;
import com.didekindroid.utils.UiUtilApp_test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComunidadSuite.class,
        GcmBroadCastNotificationActivityTest.class,
        GcmRequestTest.class,
        IncidSuiteFunctional.class,
        IncidSuiteSupport.class,
        IoHelperApp_test.class,
        UiUtilApp_test.class,
        Oauth2DaoRemoteIf_app_Test.class,
        TokenIdentityCacher_App_Test.class,
        UiAppExceptionTests.class,
        UserComuSuite.class,
        UsuarioSuite.class,
})
public class AppFullSuite {
}
