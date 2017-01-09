package com.didekindroid.incidencia;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GcmIncidAltaNotificationTest.class,
        GcmIncidCloseNotificationTest.class,
        GcmIncidResolucinOpenNotifTest.class,
        IncidCloseAc_GCM_Test.class,
        IncidenciaDataDbHelperTest.class,
        IncidRegAc_GCM_Test.class,
        IncidRegResolucion_GCM_Test.class,
        IncidSeeOpenAc_GCM_Test.class,
        IncidServiceTest_1.class,
        IncidServiceTest_2.class,
        IncidenciaBeanTest.class,
})
public class IncidSuiteSupport {
}
