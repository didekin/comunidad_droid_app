package com.didekindroid.usuario;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 23/12/16
 * Time: 11:06
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AarFBRegIntentService_Test.class,
        UsuarioDaoRemoteTest.class,
})
public class UsuarioSuiteSupport {
}
