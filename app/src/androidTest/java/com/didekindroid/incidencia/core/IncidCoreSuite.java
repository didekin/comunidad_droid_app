package com.didekindroid.incidencia.core;

import com.didekindroid.incidencia.core.edit.IncidCoreEditSuite;
import com.didekindroid.incidencia.core.reg.IncidCoreRegSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Viewers
        ViewerAmbitoIncidSpinnerTest.class,
        ViewerImportanciaSpinnerTest.class,
        // Controllers
        CtrlerAmbitoIncidSpinnerTest.class,
        CtrlerImportanciaSpinnerTest.class,
        // Helpers
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidImportanciaBeanTest.class,
        // Suites
        IncidCoreEditSuite.class,
        IncidCoreRegSuite.class,
})
public class IncidCoreSuite {
}
