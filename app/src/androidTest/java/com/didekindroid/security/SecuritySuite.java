package com.didekindroid.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        JceTests.class,
        Oauth2DaoRemoteTest.class,
        OauthTokenReactorTest.class,
        TokenIdentityCacherTest_1.class,
        TokenIdentityCacherTest_2.class,
})
public class SecuritySuite {
}
