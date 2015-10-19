package com.didekindroid.usuario.suite;

import com.didekindroid.ioutils.IoHelperTest;
import com.didekindroid.repository.MasterDataDbHelperTest;
import com.didekindroid.repository.MockDbHelperTest;
import com.didekindroid.usuario.activity.utils.IntentExtrasTests;
import com.didekindroid.usuario.dominio.ComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadBeanValidaTests;
import com.didekindroid.security.TokenHandlerTest;
import com.didekindroid.usuario.webservices.Oauth2ServiceIfTest;
import com.didekindroid.usuario.webservices.ServiceOneIfTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:27
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IntentExtrasTests.class,
        MasterDataDbHelperTest.class,
        MockDbHelperTest.class,
        IoHelperTest.class,
        TokenHandlerTest.class,
        ComunidadBeanValidaTests.class,
        UsuarioBeanValidaTests.class,
        UsuarioComunidadBeanValidaTests.class,
        ServiceOneIfTest.class,
        Oauth2ServiceIfTest.class
})
public class UserSupportTestSuite {
}
