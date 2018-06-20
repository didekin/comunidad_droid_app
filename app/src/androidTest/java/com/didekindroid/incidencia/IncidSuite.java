package com.didekindroid.incidencia;

import com.didekindroid.incidencia.comment.IncidCommentRegAcTest;
import com.didekindroid.incidencia.comment.IncidCommentSeeAcTest_1;
import com.didekindroid.incidencia.comment.IncidCommentSeeAcTest_2;
import com.didekindroid.incidencia.core.IncidCoreSuite;
import com.didekindroid.incidencia.firebase.IncidDownStreamMsgHandlerTest;
import com.didekindroid.incidencia.list.IncidListSuite;

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
        IncidCommentRegAcTest.class,
        IncidCommentSeeAcTest_1.class,
        IncidCommentSeeAcTest_2.class,
        // core
        IncidCoreSuite.class,
        // firebase
        IncidDownStreamMsgHandlerTest.class,
        // list
        IncidListSuite.class,
        // .
        IncidenciaDaoTest.class,
})
public class IncidSuite {
}
