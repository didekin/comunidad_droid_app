package com.didekindroid.usuario;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 01/12/16
 * Time: 20:28
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // .
        LoginAc_App_Test.class,
        PasswordChange_App_Test.class,
        UserData_App_Test.class,
        ViewerPswdChange_App_Test.class,
        ViewerUserData_App_Test.class,
})
public class UsuarioSuite {
}
