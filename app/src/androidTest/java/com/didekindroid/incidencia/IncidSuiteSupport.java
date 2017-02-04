package com.didekindroid.incidencia;

import com.didekindroid.incidencia.dominio.IncidenciaBeanTest;
import com.didekindroid.incidencia.firebase.IncidCloseAc_GCM_Test;
import com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandlerTest;
import com.didekindroid.incidencia.firebase.IncidRegAc_GCM_Test;
import com.didekindroid.incidencia.firebase.IncidRegResolucion_GCM_Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidCloseAc_GCM_Test.class,
        IncidenciaDataDbHelperTest.class,
        IncidFirebaseDownMsgHandlerTest.class,
        IncidRegAc_GCM_Test.class,
        IncidRegResolucion_GCM_Test.class,
        IncidServiceTest_1.class,
        IncidServiceTest_2.class,
        IncidenciaBeanTest.class,
})
public class IncidSuiteSupport {
}
