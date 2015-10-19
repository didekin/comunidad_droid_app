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
@Suite.SuiteClasses({
                ComuDataAcTest.class,
                ComuSearchResultsAcTest_1.class,
                ComuSearchAcTest_intent.class,
                ComuSearchTest_spinner.class,
                DeleteMeAcTest.class,
                LoginAcTest.class,
                PasswordChangeAcTest.class,
                RegUserComuAcTest.class,
                RegUserComuAcTest_intent.class,
                RegUserAndUserComuAcTest_intent.class,
                RegComuAndUserComuAcTest.class,
                RegComuAndUserAndUserComuAcTest.class,
                SeeUserComuByComuAcTest.class,
                SeeUserComuByUserAcTest.class,  // En trámite.
                UserComuDataAcTest_1.class,
                UserComuDataAcTest_2.class,
                UserDataAcTest.class
        })
public class UserFunctionalTestSuite {
}
