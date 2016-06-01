package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.gcm.IncidenciaGcmNotificationTest;
import com.didekindroid.incidencia.gcm.IncidenciaGcmResponseTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/06/16
 * Time: 14:06
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidenciaGcmResponseTest.class,
        IncidenciaGcmNotificationTest.class,
})
public class IncidenciaGcmTestSuite {
}
