package com.didekindroid.usuario;

import com.didekindroid.usuario.delete.DeleteMeAppAc_App_Test;
import com.didekindroid.usuario.login.LoginAc_App_1_Test;
import com.didekindroid.usuario.login.LoginAppAcObservableTest;
import com.didekindroid.usuario.password.PasswordChangeAc_App_Test;
import com.didekindroid.usuario.userdata.UserDataAc_App_Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegGcmIntentService_Test.class,
        DeleteMeAppAc_App_Test.class,
        LoginAc_App_1_Test.class,
        LoginAppAcObservableTest.class,
        PasswordChangeAc_App_Test.class,
        UserDataAc_App_Test.class,
        UsuarioBeanValida_App_test.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuite {
}
