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
@Suite.SuiteClasses(
        {
                ComuDataAcTest.class,
                ComuSearchAcTest.class,
                ComuSearchAcTest_intent.class,
                ComuSearchTest_spinner.class,
                ComuSearchResultsAcTest.class,
                DeleteMeAcTest.class,
                PasswordChangeAcTest.class,
                RegUserComuAcTest.class,
                RegUserComuAcTest_intent.class,
                RegUserAndUserComuAcTest_intent.class,
                RegComuAndUserComuAcTest.class,
                RegComuAndUserAndUserComuAcTest.class,
                UserDataAcTest.class,
                SeeUserComuByComuAcTest.class,
                SeeUserComuByUserAcTest.class  // En trámite.
        }
)
public class UserFunctionalTestSuite {
}
