package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest;
import com.didekindroid.incidencia.webservices.IncidServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidServiceTest.class,
})
public class IncidSupportTestSuite {
}
