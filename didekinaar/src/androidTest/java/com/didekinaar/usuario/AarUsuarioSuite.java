package com.didekinaar.usuario;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:49
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AarFBRegIntentService_Test.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
})
public class AarUsuarioSuite {
}
