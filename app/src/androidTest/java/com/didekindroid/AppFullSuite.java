package com.didekindroid;

import com.didekindroid.accesorio.ConfidencialidadAcTest;
import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.security.TokenIdentityCacher_app_Test;
import com.didekindroid.usuario.ViewerUserDrawer_App_Test;
import com.didekindroid.router.ContextualActionTest;
import com.didekindroid.router.MnRouterActionTest;
import com.didekindroid.router.UiExceptionActionTest;
import com.didekindroid.usuario.UsuarioSuite;
import com.didekindroid.usuariocomunidad.UserComuSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // accesorio.
        ConfidencialidadAcTest.class,
        // comunidad
        ComunidadSuite.class,
        // incidencia
        IncidSuite.class,
        // router.
        ContextualActionTest.class,
        MnRouterActionTest.class,
        UiExceptionActionTest.class,
        ViewerUserDrawer_App_Test.class,
        // security.
        TokenIdentityCacher_app_Test.class,
        // usuario
        UsuarioSuite.class,
        // usuariocomunidad
        UserComuSuite.class,
})
public class AppFullSuite {
}
