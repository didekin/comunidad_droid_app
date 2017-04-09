package com.didekindroid.incidencia.core.reg;

import com.didekindroid.incidencia.core.CtrlerAmbitoIncidSpinnerTest;
import com.didekindroid.incidencia.core.CtrlerImportanciaSpinnerTest;
import com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinnerTest;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinnerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 07/04/17
 * Time: 17:56
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Activities.
        IncidRegAcTest_1.class,
        // Viewers
        ViewerIncidRegAcTest.class,
        ViewerIncidRegFrTest.class,
        // Controllers
        CtrlerIncidRegAcTest.class,
        // GCM
        IncidRegAc_GCM_Test.class,
})
public class IncidCoreRegSuite {
}
