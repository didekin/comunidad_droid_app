package com.didekindroid;

import com.didekindroid.common.suite.CommonSuite_1;
import com.didekindroid.incidencia.suite.IncidFunctionalSlowTestSuite;
import com.didekindroid.incidencia.suite.IncidFunctionalTestSuite;
import com.didekindroid.incidencia.suite.IncidSupportTestSuite;
import com.didekindroid.usuario.suite.UserFunctionalSlowTestSuite;
import com.didekindroid.usuario.suite.UserFunctionalTestSuite;
import com.didekindroid.usuario.suite.UserSupportTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CommonSuite_1.class,
        IncidFunctionalTestSuite.class,
        IncidFunctionalSlowTestSuite.class,
        IncidSupportTestSuite.class,
        UserFunctionalTestSuite.class,
        UserFunctionalSlowTestSuite.class,
        UserSupportTestSuite.class,
})
public class AppFullTestSuit {
}
