package com.didekindroid.usuario.suite;

import com.didekindroid.common.ui.IntentExtrasTests;
import com.didekindroid.masterdata.IoHelperTest;
import com.didekindroid.masterdata.repository.MasterDataDbHelperTest;
import com.didekindroid.masterdata.repository.MockDbHelperTest;
import com.didekindroid.usuario.dominio.ComunidadBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioBeanValidaTests;
import com.didekindroid.usuario.dominio.UsuarioComunidadBeanValidaTests;
import com.didekindroid.usuario.common.TokenHandlerTest;
import com.didekindroid.usuario.webservices.ServiceOneTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:27
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({IntentExtrasTests.class, MasterDataDbHelperTest.class, MockDbHelperTest.class, IoHelperTest
        .class, TokenHandlerTest.class, ServiceOneTest.class, ComunidadBeanValidaTests
        .class, UsuarioBeanValidaTests.class, UsuarioComunidadBeanValidaTests.class})
public class UserSupportTestSuite {
}
