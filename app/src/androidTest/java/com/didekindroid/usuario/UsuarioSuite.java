package com.didekindroid.usuario;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DeleteMeAcTest.class,
        LoginAcTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        ViewerLoginTest.class,
        ViewerPasswordChangeTest.class,
        ViewerRegUserFrTest.class,
        ViewerUserDataTest.class,
})
public class UsuarioSuite {
}
