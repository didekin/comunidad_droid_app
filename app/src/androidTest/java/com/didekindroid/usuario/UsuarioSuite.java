package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.delete.DeleteMeReactorTest;
import com.didekindroid.usuario.firebase.FirebaseTokenReactorTest;
import com.didekindroid.usuario.login.LoginAc_1_Test;
import com.didekindroid.usuario.login.LoginReactorTest;
import com.didekindroid.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuario.userdata.UserDataAcTest;

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
        FirebaseTokenReactorTest.class,
//        LoginAcTest.class,
        LoginAc_1_Test.class,
        LoginReactorTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuite {
}
