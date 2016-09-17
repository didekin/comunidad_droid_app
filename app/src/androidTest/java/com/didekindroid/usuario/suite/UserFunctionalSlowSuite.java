package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchAc_3_SlowTest;
import com.didekindroid.usuario.activity.ComuSearchResultsAc_2_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_2_SlowTest;
import com.didekindroid.usuario.activity.LoginAc_3_SlowTest;

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
        ComuSearchAc_3_SlowTest.class,
        ComuSearchResultsAc_2_SlowTest.class,
        LoginAc_2_SlowTest.class,
        LoginAc_3_SlowTest.class,
})
public class UserFunctionalSlowSuite {
}
