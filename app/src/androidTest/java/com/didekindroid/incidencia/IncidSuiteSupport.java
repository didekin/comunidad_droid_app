package com.didekindroid.incidencia;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;
import com.didekindroid.incidencia.firebase.GcmIncidAltaNotificationTest;
import com.didekindroid.incidencia.firebase.GcmIncidCloseNotificationTest;
import com.didekindroid.incidencia.firebase.GcmIncidResolucinOpenNotifTest;
import com.didekindroid.incidencia.firebase.IncidCloseAc_GCM_Test;
import com.didekindroid.incidencia.firebase.IncidRegAc_GCM_Test;
import com.didekindroid.incidencia.firebase.IncidRegResolucion_GCM_Test;
import com.didekindroid.incidencia.firebase.IncidSeeOpenAc_GCM_Test;

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
