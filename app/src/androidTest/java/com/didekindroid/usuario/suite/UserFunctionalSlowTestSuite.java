package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchAcTest_slow;
import com.didekindroid.usuario.activity.ComuSearchResultsAcTest_slow;
import com.didekindroid.usuario.activity.LoginAcTest_2;
import com.didekindroid.usuario.activity.LoginAcTest_3;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAcTest_slow;
import com.didekindroid.usuario.activity.SeeUserComuByComuAcTest_slow;
import com.didekindroid.usuario.activity.SeeUserComuByUserAcTest_slow;
import com.didekindroid.usuario.activity.UserDataAcTest_slow;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/10/15
 * Time: 18:13
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComuSearchAcTest_slow.class,
        ComuSearchResultsAcTest_slow.class,
        LoginAcTest_2.class,
        LoginAcTest_3.class,
        RegComuAndUserAndUserComuAcTest_slow.class,
        SeeUserComuByComuAcTest_slow.class,
        SeeUserComuByUserAcTest_slow.class,
        UserDataAcTest_slow.class,
})
public class UserFunctionalSlowTestSuite {
}
