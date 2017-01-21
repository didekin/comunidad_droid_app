package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;

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
        LoginAppAcObservableTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuite {
}
