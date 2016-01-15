package com.didekindroid.incidencia.suite;

import com.didekindroid.common.suite.CommonSuite_1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/11/15
 * Time: 18:55
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IncidFunctionalTestSuite.class,
        IncidSupportTestSuite.class,
        CommonSuite_1.class,
})
public class IncidFullTestSuite {
}
