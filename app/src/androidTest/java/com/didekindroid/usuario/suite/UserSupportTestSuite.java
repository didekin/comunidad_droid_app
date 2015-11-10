package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.repository.MasterDataDbHelperTest;
import com.didekindroid.usuario.repository.MockDbHelperTest;
import com.didekindroid.common.TokenHandlerTest;
import com.didekindroid.usuario.activity.utils.IntentExtrasTests;
import com.didekindroid.usuario.dominio.ComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.ComunidadTest;
import com.didekindroid.usuario.dominio.MunicipioTest;
import com.didekindroid.usuario.dominio.UsuarioBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadTest;
import com.didekindroid.usuario.dominio.UsuarioTest;
import com.didekindroid.usuario.webservices.Oauth2ServiceIfTest;
import com.didekindroid.usuario.webservices.ServiceOneIfTest;
import com.didekindroid.common.utils.IoHelperTest;
import com.didekindroid.common.utils.UIutilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:27
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComunidadTest.class,
        ComunidadBeanValidaTests.class,
        IntentExtrasTests.class,
        IoHelperTest.class,
        MasterDataDbHelperTest.class,
        MockDbHelperTest.class,
        MunicipioTest.class,
        Oauth2ServiceIfTest.class,
        ServiceOneIfTest.class,
        TokenHandlerTest.class,
        UIutilsTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioComunidadBeanValidaTests.class,
        UsuarioComunidadTest.class,
        UsuarioTest.class,
})
public class UserSupportTestSuite {
}
