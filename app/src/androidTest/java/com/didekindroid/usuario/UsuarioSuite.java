package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.delete.DeleteMeReactorTest;
import com.didekindroid.usuario.firebase.FirebaseTokenReactorTest;
import com.didekindroid.usuario.login.LoginAc_1_Test;
import com.didekindroid.usuario.login.LoginReactorTest;
import com.didekindroid.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuario.password.PswdChangeReactorTest;
import com.didekindroid.usuario.userdata.UserDataAcTest;
import com.didekindroid.usuario.userdata.UserDataReactorTest;

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
//        LoginAcTest.class,
        LoginAc_1_Test.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
        // Reactors.
        DeleteMeReactorTest.class,
        FirebaseTokenReactorTest.class,
        LoginReactorTest.class,
        PswdChangeReactorTest.class,
        UserDataReactorTest.class,
})
public class UsuarioSuite {
}
