package com.didekinaar.usuario;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:49
 */
@SuppressWarnings("EmptyClass")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AarFBRegIntentService_Test.class,
        AarUsuarioServiceTest.class,
        DeleteMeAcTest.class,
        LoginAc_1_Test.class,
        LoginAc_2_SlowTest.class,
        LoginAc_3_SlowTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioTest.class,
})
public class AarUsuarioSuite {
}
