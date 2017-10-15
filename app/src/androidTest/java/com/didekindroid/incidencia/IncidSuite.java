package com.didekindroid.incidencia;

import com.didekindroid.incidencia.comment.IncidCommentSuite;
import com.didekindroid.incidencia.core.IncidCoreSuite;
import com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandlerTest;
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
        // comment
        IncidCommentSuite.class,
        // core
        IncidCoreSuite.class,
        // firebase
        IncidDownStreamMsgHandlerTest.class,
        // list
        IncidListSuite.class,
        // resolucion
        IncidResolucionSuite.class,
        // .
        IncidDaoRemoteTest_1.class,
        IncidDaoRemoteTest_2.class,
        IncidObservableTest.class,
})
public class IncidSuite {
}
