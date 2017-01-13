package com.didekindroid;

import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.exception.UiAppExceptionTests;
import com.didekindroid.incidencia.IncidSuiteFunctional;
import com.didekindroid.incidencia.IncidSuiteSupport;
import com.didekindroid.security.Oauth2DaoRemoteIf_app_Test;
import com.didekindroid.security.TokenIdentityCacher_App_Test;
import com.didekindroid.usuario.UsuarioSuite;
import com.didekindroid.usuariocomunidad.UserComuSuite;
import com.didekindroid.utils.IoHelperTest;
import com.didekindroid.utils.UIutilsTest;

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
        IncidSuiteFunctional.class,
        IncidSuiteSupport.class,
        IoHelperTest.class,
        UIutilsTest.class,
        Oauth2DaoRemoteIf_app_Test.class,
        TokenIdentityCacher_App_Test.class,
        UiAppExceptionTests.class,
        UserComuSuite.class,
        UsuarioSuite.class,
})
public class AppFullSuite {
}
