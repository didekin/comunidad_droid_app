package com.didekindroid.incidencia;

import com.didekindroid.gcm.GcmBroadCastNotificationActivityTest;
import com.didekindroid.gcm.GcmRequestTest;
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
        GcmBroadCastNotificationActivityTest.class,
        GcmRequestTest.class,
        IncidCloseAc_GCM_Test.class,
        IncidRegAc_GCM_Test.class,
        IncidRegResolucion_GCM_Test.class,
        IncidSeeOpenAc_GCM_Test.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidServiceTest_1.class,
        IncidServiceTest_2.class,
})
public class IncidSuiteSupport {
}
