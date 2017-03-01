package com.didekindroid;

import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.exception.UiAppExceptionTests;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.security.JceTests;
import com.didekindroid.security.Oauth2DaoRemoteTest;
import com.didekindroid.security.OauthTokenReactor_1_Test;
import com.didekindroid.security.TokenIdentityCacherTest_1;
import com.didekindroid.security.TokenIdentityCacherTest_2;
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
        IncidSuite.class,
        IoHelperTest.class,
        JceTests.class,
        Oauth2DaoRemoteTest.class,
        OauthTokenReactor_1_Test.class,
        TokenIdentityCacherTest_1.class,
        TokenIdentityCacherTest_2.class,
        UiAppExceptionTests.class,
        UIutilsTest.class,
        UserComuSuite.class,
        UsuarioSuite.class,
})
public class AppFullSuite {
}
