package com.didekindroid;

import com.didekindroid.incidencia.IncidSuiteFunctional;
import com.didekindroid.incidencia.IncidSuiteSupport;
import com.didekindroid.usuario.UsuarioSuite;
import com.didekindroid.usuario.UsuarioSuiteSupport;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidSuiteFunctional.class,
        IncidSuiteSupport.class,
        UsuarioSuite.class,
        UsuarioSuiteSupport.class,
})
public class AppFullSuite {
}
