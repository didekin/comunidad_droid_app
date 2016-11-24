package com.didekinaar;

import com.didekinaar.comunidad.AarComunidadSuite;
import com.didekinaar.usuario.AarUsuarioSuite;
import com.didekinaar.usuariocomunidad.AarUserComuSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:01
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AarComunidadSuite.class,
        AarCommonSuite.class,
        AarUsuarioSuite.class,
        AarUserComuSuite.class,
})
public class AarSuite {
}
