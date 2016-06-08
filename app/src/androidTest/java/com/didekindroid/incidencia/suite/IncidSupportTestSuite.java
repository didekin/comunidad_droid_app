package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;
import com.didekindroid.incidencia.gcm.GcmIncidNotificationTest_1;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelperTest;
import com.didekindroid.incidencia.webservices.IncidServiceTest_1;
import com.didekindroid.incidencia.webservices.IncidServiceTest_2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GcmIncidNotificationTest_1.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidServiceTest_1.class,
        IncidServiceTest_2.class,
})
public class IncidSupportTestSuite {
}
