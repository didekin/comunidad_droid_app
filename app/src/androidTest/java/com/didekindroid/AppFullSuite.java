package com.didekindroid;

import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.router.DidekinContextActionTest;
import com.didekindroid.router.DidekinMnActionTest;
import com.didekindroid.router.DidekinUiExceptionActionTest;
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
        // comunidad
        ComunidadSuite.class,
        // incidencia
        IncidSuite.class,
        // router.
        DidekinContextActionTest.class,
        DidekinMnActionTest.class,
        DidekinUiExceptionActionTest.class,
        // usuario
        UsuarioSuite.class,
        // usuariocomunidad
        UserComuSuite.class,
})
public class AppFullSuite {
}
