package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchAcTest;
import com.didekindroid.usuario.activity.ComuSearchAcTest_B;
import com.didekindroid.usuario.activity.ComuSearchResultsAcTest;
import com.didekindroid.usuario.activity.RegUserComuAcTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:49
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ComuSearchAcTest.class, ComuSearchAcTest_B.class, ComuSearchResultsAcTest.class,
        RegUserComuAcTest.class})
public class UserFunctionalTestSuite {
}
