package com.didekindroid.lib_one.security;

import com.didekindroid.security.AuthDaoRemoteTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        CtrlerAuthTokenTest.class,
        JceTests.class,
        AuthDaoRemoteTest.class,
        OauthTokenObservableTest.class,
        TokenIdentityCacherTest_1.class,
/*        TokenIdentityCacherTest_2.class,*/
})
public class SecuritySuite {
}
