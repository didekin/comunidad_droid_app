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
        // comunidad
        ComunidadSuite.class,
        // incidencia
        IncidSuite.class,
        // security
        SecuritySuite.class,
        /* usuario*/
        UsuarioSuite.class,
        // usuariocomunidad
        UserComuSuite.class,
        // miscelánea: api, accesorio, exception, ...
        MiscSuite.class,
})
public class AppFullSuite {
}
