package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:49
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComuSearchAcTest.class, ComuSearchAcTest_B.class, ComuSearchResultsAcTest.class,
        RegUserComuAcTest.class, SeeUserComuByComuAcTest.class})
public class UserFunctionalTestSuite {
}
