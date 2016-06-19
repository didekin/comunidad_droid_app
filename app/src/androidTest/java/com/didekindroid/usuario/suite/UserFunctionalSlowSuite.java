package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchAc_1_SlowTest;
import com.didekindroid.usuario.activity.ComuSearchAc_2_SlowTest;
import com.didekindroid.usuario.activity.ComuSearchResultsAc_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_2_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_3_SlowTest;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAc_SlowTest;
import com.didekindroid.usuario.activity.SeeUserComuByComuAc_SlowTest;
import com.didekindroid.usuario.activity.SeeUserComuByUserAc_SlowTest;
import com.didekindroid.usuario.activity.UserDataAc_SlowTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/10/15
 * Time: 18:13
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComuSearchAc_1_SlowTest.class,
        ComuSearchAc_2_SlowTest.class,
        ComuSearchResultsAc_SlowTest.class,
        LoginAc_2_SlowTest.class,
        LoginAc_3_SlowTest.class,
        RegComuAndUserAndUserComuAc_SlowTest.class,
        SeeUserComuByComuAc_SlowTest.class,
        SeeUserComuByUserAc_SlowTest.class,
        UserDataAc_SlowTest.class,
})
public class UserFunctionalSlowSuite {
}
