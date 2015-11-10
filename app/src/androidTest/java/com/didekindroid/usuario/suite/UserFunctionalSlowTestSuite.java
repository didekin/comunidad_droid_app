package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.activity.ComuSearchResultsAcTest_2;
import com.didekindroid.usuario.activity.LoginAcTest_2;
import com.didekindroid.usuario.activity.LoginAcTest_3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 16/10/15
 * Time: 18:13
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComuSearchResultsAcTest_2.class,
        LoginAcTest_2.class,
        LoginAcTest_3.class
})
public class UserFunctionalSlowTestSuite {
}
