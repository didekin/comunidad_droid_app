package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.CtrlerDeleteMeTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenTest;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenTest;
import com.didekindroid.usuario.login.CtrlerUsuario_Login_Test;
import com.didekindroid.usuario.login.LoginAcTest;
import com.didekindroid.usuario.login.ViewerLoginTest;
import com.didekindroid.usuario.password.CtrlerUsuario_PasswordChange_Test;
import com.didekindroid.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuario.password.ViewerPasswordChangeTest;
import com.didekindroid.usuario.userdata.CtrlerUserModifiedTest;
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
        // dao
        UsuarioDaoRemoteTest.class,
        // delete
        CtrlerDeleteMeTest.class,
        DeleteMeAcTest.class,
        // firebase
        CtrlerFirebaseTokenTest.class,
        ViewerFirebaseTokenTest.class,
        // login
        CtrlerUsuario_Login_Test.class,
        LoginAcTest.class,
        ViewerLoginTest.class,
        // password
        CtrlerUsuario_PasswordChange_Test.class,
        PasswordChangeAcTest.class,
        ViewerPasswordChangeTest.class,
        // userdata
        CtrlerUserModifiedTest.class,
        UserDataAcTest.class,
        ViewerUserDataTest.class,
        // .
        UsuarioBeanValidaTests.class,
        ViewerRegUserFrTest.class,

})
public class UsuarioSuite {
}
