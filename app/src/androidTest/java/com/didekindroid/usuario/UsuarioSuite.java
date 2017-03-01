package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.ControllerDeleteMeTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.delete.ReactorDeleteMeTest;
import com.didekindroid.usuario.firebase.FirebaseTokenReactorTest;
import com.didekindroid.usuario.login.ControllerLoginTest;
import com.didekindroid.usuario.login.LoginAc_1_Test;
import com.didekindroid.usuario.login.ReactorLoginTest;
import com.didekindroid.usuario.password.ControllerPasswordChangeTest;
import com.didekindroid.usuario.password.PasswordChangeAc_Integration_Test;
import com.didekindroid.usuario.password.PasswordChangeAc_Unit_Test;
import com.didekindroid.usuario.password.ReactorPswdChangeTest;
import com.didekindroid.usuario.userdata.ControllerUserDataTest;
import com.didekindroid.usuario.userdata.UserDataAcTest;
import com.didekindroid.usuario.userdata.ReactorUserDataTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        ControllerDeleteMeTest.class,
        ControllerLoginTest.class,
        ControllerPasswordChangeTest.class,
        ControllerUserDataTest.class,
        DeleteMeAcTest.class,
//        LoginAcTest.class,
        LoginAc_1_Test.class,
        PasswordChangeAc_Integration_Test.class,
        PasswordChangeAc_Unit_Test.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
        // Reactors.
        FirebaseTokenReactorTest.class,
        ReactorDeleteMeTest.class,
        ReactorLoginTest.class,
        ReactorPswdChangeTest.class,
        ReactorUserDataTest.class,
})
public class UsuarioSuite {
}
