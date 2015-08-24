package com.didekindroid.usuario.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:43
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UserSupportTestSuite.class, UserFunctionalTestSuite.class})
public class UserFullTestSuite {
}
