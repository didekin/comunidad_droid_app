package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.ControllerDeleteMeTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.login.CtrlerLoginTest;
import com.didekindroid.usuario.login.LoginAcTest;
import com.didekindroid.usuario.password.CtrlerPasswordChangeTest;
import com.didekindroid.usuario.password.PasswordChangeAc_Test;
import com.didekindroid.usuario.userdata.CtrlerUserDataTest;
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

        ControllerDeleteMeTest.class,
        CtrlerLoginTest.class,
        CtrlerPasswordChangeTest.class,
        CtrlerUserDataTest.class,
        DeleteMeAcTest.class,
        LoginAcTest.class,
        PasswordChangeAc_Test.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuite {
}
