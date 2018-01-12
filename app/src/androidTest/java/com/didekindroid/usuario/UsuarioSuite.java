package com.didekindroid.usuario;

import com.didekindroid.usuario.dao.CtrlerUsuario_Test;
import com.didekindroid.usuario.dao.UsuarioObservableTest;
import com.didekindroid.usuario.dao.UsuarioDaoRemoteTest;
import com.didekindroid.usuario.delete.DeleteMeAcTest;
import com.didekindroid.usuario.firebase.CtrlerFirebaseTokenTest;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenTest;
import com.didekindroid.usuario.login.LoginAcTest;
import com.didekindroid.usuario.login.PasswordMailDialogTest;
import com.didekindroid.usuario.login.ViewerLoginTest;
import com.didekindroid.usuario.password.PasswordChangeAcTest;
import com.didekindroid.usuario.password.ViewerPasswordChangeTest;
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
        CtrlerUsuario_Test.class,
        UsuarioObservableTest.class,
        UsuarioDaoRemoteTest.class,
        // delete
        DeleteMeAcTest.class,
        // firebase
        CtrlerFirebaseTokenTest.class,
        ViewerFirebaseTokenTest.class,
        // login
        LoginAcTest.class,
        PasswordMailDialogTest.class,
        ViewerLoginTest.class,
        // password
        PasswordChangeAcTest.class,
        ViewerPasswordChangeTest.class,
        // userdata
        UserDataAcTest.class,
        ViewerUserDataTest.class,
        // .
        UsuarioBeanTests.class,
        ViewerRegUserFrTest.class,

})
public class UsuarioSuite {
}
