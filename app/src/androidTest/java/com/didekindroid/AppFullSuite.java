package com.didekindroid;

import com.didekindroid.common.suite.CommonSuite;
import com.didekindroid.incidencia.suite.IncidFunctionalSuite;
import com.didekindroid.incidencia.suite.IncidSupportSuite;
import com.didekindroid.usuario.suite.UserFunctionalSlowSuite;
import com.didekindroid.usuario.suite.UserFunctionalSuite;
import com.didekindroid.usuario.suite.UserSupportSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 12:47
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CommonSuite.class,
        IncidFunctionalSuite.class,
        IncidSupportSuite.class,
        UserFunctionalSuite.class,
        UserFunctionalSlowSuite.class,
        UserSupportSuite.class,
})
public class AppFullSuite {
}
