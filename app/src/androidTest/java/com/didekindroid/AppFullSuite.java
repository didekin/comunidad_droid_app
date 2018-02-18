package com.didekindroid;

import com.didekindroid.accesorio.ConfidencialidadAcTest;
import com.didekindroid.comunidad.ComunidadSuite;
import com.didekindroid.incidencia.IncidSuite;
import com.didekindroid.router.MnRouterActionTest;
import com.didekindroid.router.UiExceptionActionTest;
import com.didekindroid.router.ViewerDrawerMain_NotReg_Test;
import com.didekindroid.router.ViewerDrawerMain_Reg_Test;
import com.didekindroid.lib_one.usuario.UsuarioSuite;
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
        MnRouterActionTest.class,
        UiExceptionActionTest.class,
        ViewerDrawerMain_NotReg_Test.class,
        ViewerDrawerMain_Reg_Test.class,
        // usuario
        UsuarioSuite.class,
        // usuariocomunidad
        UserComuSuite.class,
})
public class AppFullSuite {
}
