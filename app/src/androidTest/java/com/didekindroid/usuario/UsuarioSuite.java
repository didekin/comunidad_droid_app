package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.delete.DeleteMeReactorTest;
import com.didekindroid.usuario.login.LoginAcReactorTest;
import com.didekindroid.usuario.login.LoginAcTest;

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
        DeleteMeReactorTest.class,
        LoginAcTest.class,
        LoginAcReactorTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuite {
}
