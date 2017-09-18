package com.didekindroid;

import com.didekindroid.accesorio.ConfidencialidadAcTest;
import com.didekindroid.api.ApiSuite;
import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.exception.UiExceptionTest;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.router.ActivityInitiatorTest;
import com.didekindroid.router.ActivityRouterTest;
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
        // Accesorio.
        ConfidencialidadAcTest.class,
        // api.
        ApiSuite.class,
        // comunidad
        ComunidadSuite.class,
        // exception
        UiExceptionTest.class,
        // incidencia
        IncidSuite.class,
        // router.
        ActivityInitiatorTest.class,
        ActivityRouterTest.class,
        // security
        SecuritySuite.class,
        /* usuario*/
        UsuarioSuite.class,
        // usuariocomunidad
        UserComuSuite.class,
        // utils
        IoHelperTest.class,
        UIutilsTest.class,
})
public class AppFullSuite {
}
