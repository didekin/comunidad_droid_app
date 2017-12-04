package com.didekindroid.incidencia.resolucion;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 13:22
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidRegResolucion_GCM_Test.class,
        IncidResolucionEditFrTest.class,
        IncidResolucionRegAcTest.class,
        IncidResolucionSeeFrTest.class,
})
public class IncidResolucionSuite {
}
