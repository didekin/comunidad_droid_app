package com.didekindroid.incidencia.suite;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;
import com.didekindroid.incidencia.gcm.GcmIncidAltaNotificationTest;
import com.didekindroid.incidencia.gcm.GcmIncidCloseNotificationTest;
import com.didekindroid.incidencia.gcm.GcmIncidResolucinOpenNotifTest;
import com.didekindroid.incidencia.gcm.IncidCloseAc_GCM_Test;
import com.didekindroid.incidencia.gcm.IncidRegAc_GCM_Test;
import com.didekindroid.incidencia.gcm.IncidRegResolucion_GCM_Test;
import com.didekindroid.incidencia.gcm.IncidSeeOpenAc_GCM_Test;
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
        GcmIncidAltaNotificationTest.class,
        GcmIncidCloseNotificationTest.class,
        GcmIncidResolucinOpenNotifTest.class,
        IncidCloseAc_GCM_Test.class,
        IncidRegAc_GCM_Test.class,
        IncidRegResolucion_GCM_Test.class,
        IncidSeeOpenAc_GCM_Test.class,
        IncidenciaBeanTest.class,
        IncidenciaDataDbHelperTest.class,
        IncidServiceTest_1.class,
        IncidServiceTest_2.class,
})
public class IncidSupportSuite {
}
