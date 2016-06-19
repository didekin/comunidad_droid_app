package com.didekindroid.usuario.suite;

import com.didekindroid.usuario.dominio.ComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.ComunidadTest;
import com.didekindroid.usuario.dominio.MunicipioTest;
import com.didekindroid.usuario.dominio.UsuarioBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadTest;
import com.didekindroid.usuario.dominio.UsuarioTest;
import com.didekindroid.usuario.repository.MockDbHelperTest;
import com.didekindroid.usuario.repository.UsuarioDataDbHelperTest;
import com.didekindroid.usuario.webservices.UsuarioServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:27
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComunidadBeanValidaTests.class,
        ComunidadTest.class,
        MockDbHelperTest.class,
        MunicipioTest.class,
        UsuarioBeanValidaTests.class,
        UsuarioComunidadBeanValidaTests.class,
        UsuarioComunidadTest.class,
        UsuarioDataDbHelperTest.class,
        UsuarioServiceTest.class,
        UsuarioTest.class,
})
public class UserSupportSuite {
}
