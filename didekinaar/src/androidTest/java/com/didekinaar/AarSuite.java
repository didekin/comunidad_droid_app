package com.didekinaar;

import com.didekinaar.mock.MockDbHelperTest;
import com.didekinaar.security.JceTests;
import com.didekinaar.security.Oauth2ServiceIfTest;
import com.didekinaar.security.TokenHandlerTest;
import com.didekinaar.usuario.UsuarioBeanValidaTests;
import com.didekinaar.utils.IoHelperTest;
import com.didekinaar.utils.UIutilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 20/08/15
 * Time: 09:27
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IoHelperTest.class,
        JceTests.class,
        MockDbHelperTest.class,
        Oauth2ServiceIfTest.class,
        TokenHandlerTest.class,
        UIutilsTest.class,
        UsuarioBeanValidaTests.class,
})
public class AarSuite {
}
