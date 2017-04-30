package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.CtrlerDeleteMeTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenTest;
import com.didekindroid.usuario.login.CtrlerLoginTest;
import com.didekindroid.usuario.login.LoginAcTest;
import com.didekindroid.usuario.login.ViewerLoginTest;
import com.didekindroid.usuario.password.CtrlerPasswordChangeTest;
import com.didekindroid.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuario.password.ViewerPasswordChangeTest;
import com.didekindroid.usuario.userdata.CtrlerUserDataTest;
import com.didekindroid.usuario.userdata.UserDataAcTest;
import com.didekindroid.usuario.userdata.ViewerUserDataTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({

        CtrlerDeleteMeTest.class,
        CtrlerFirebaseTokenTest.class,
        CtrlerLoginTest.class,
        CtrlerPasswordChangeTest.class,
        CtrlerUserDataTest.class,
        DeleteMeAcTest.class,
        LoginAcTest.class,
        PasswordChangeAcTest.class,
        UserDataAcTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioDaoRemoteTest.class,
        ViewerLoginTest.class,
        ViewerPasswordChangeTest.class,
        ViewerUserDataTest.class,
})
public class UsuarioSuite {
}
