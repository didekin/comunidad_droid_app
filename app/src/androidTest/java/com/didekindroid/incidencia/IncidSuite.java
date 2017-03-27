package com.didekindroid.incidencia;

import com.didekindroid.incidencia.comment.IncidCommentSuite;
import com.didekindroid.incidencia.core.IncidCoreSuite;
import com.didekindroid.incidencia.firebase.IncidFirebaseDownMsgHandlerTest;
import com.didekindroid.incidencia.list.IncidListSuite;
import com.didekindroid.incidencia.resolucion.IncidResolucionSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidCommentSuite.class,
        IncidCoreSuite.class,
        IncidDaoRemoteTest_1.class,
        IncidDaoRemoteTest_2.class,
        IncidFirebaseDownMsgHandlerTest.class,
        IncidListSuite.class,
        IncidResolucionSuite.class,
})
public class IncidSuite {
}
