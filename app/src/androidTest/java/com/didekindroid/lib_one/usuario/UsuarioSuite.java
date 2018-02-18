package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.usuario.dao.CtrlerUsuario_Test;
import com.didekindroid.lib_one.usuario.dao.UsuarioObservableTest;
import com.didekindroid.lib_one.usuario.dao.UsuarioDaoTest;
import com.didekindroid.usuario.DeleteMeAcTest;
import com.didekindroid.lib_one.usuario.notification.CtrlerNotifyTokenTest;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyTokenTest;
import com.didekindroid.usuario.LoginAcTest;
import com.didekindroid.usuario.ViewerLoginTest;
import com.didekindroid.usuario.PasswordChangeAcTest;
import com.didekindroid.usuario.ViewerPasswordChangeTest;
import com.didekindroid.usuario.UserDataAcTest;
import com.didekindroid.usuario.ViewerRegUserFrTest;
import com.didekindroid.usuario.ViewerUserDataTest;

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
        UsuarioDaoTest.class,
        // delete
        DeleteMeAcTest.class,
        // firebase
        CtrlerNotifyTokenTest.class,
        ViewerNotifyTokenTest.class,
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
