package com.didekindroid.usuario.suite;

import com.didekindroid.common.suite.CommonSuite_1;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:43
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CommonSuite_1.class,
        UserSupportTestSuite.class,
        UserFunctionalTestSuite.class,
        UserFunctionalSlowTestSuite.class
})
public class UserFullTestSuite {
}
