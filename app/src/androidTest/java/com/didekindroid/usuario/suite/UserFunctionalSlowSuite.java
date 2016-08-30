package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchAc_5_SlowTest;
import com.didekindroid.usuario.activity.ComuSearchAc_6_SlowTest;
import com.didekindroid.usuario.activity.ComuSearchResultsAc_4_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_2_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_3_SlowTest;
import com.didekindroid.usuario.activity.RegComuAndUserAndUserComuAc_6_SlowTest;
import com.didekindroid.usuario.activity.SeeUserComuByComuAc_3_SlowTest;
import com.didekindroid.usuario.activity.SeeUserComuByUserAc_2_SlowTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/10/15
 * Time: 18:13
 */
@SuppressWarnings("EmptyClass")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComuSearchAc_6_SlowTest.class,
        ComuSearchAc_5_SlowTest.class,
        ComuSearchResultsAc_4_SlowTest.class,
        LoginAc_2_SlowTest.class,
        LoginAc_3_SlowTest.class,
        RegComuAndUserAndUserComuAc_6_SlowTest.class,
        SeeUserComuByComuAc_3_SlowTest.class,
        SeeUserComuByUserAc_2_SlowTest.class
})
public class UserFunctionalSlowSuite {
}
