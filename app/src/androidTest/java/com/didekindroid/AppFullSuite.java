package com.didekindroid;

import com.didekindroid.api.ApiSuite;
import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.exception.UiExceptionTest;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.router.ActivityInitiatorTest;
import com.didekindroid.security.SecuritySuite;
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
        ActivityInitiatorTest.class,
        ApiSuite.class,
        ComunidadSuite.class,
        IncidSuite.class,
        IoHelperTest.class,
        SecuritySuite.class,
        UiExceptionTest.class,
        UIutilsTest.class,
        UserComuSuite.class,
        UsuarioSuite.class,
})
public class AppFullSuite {
}
