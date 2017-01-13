package com.didekinaar;

import com.didekinaar.usuario.OauthTokenObservableTest;
import com.didekinaar.usuario.UsuarioTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 06/12/16
 * Time: 11:44
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UsuarioTest.class,
        OauthTokenObservableTest.class
})
public class SimpleUsarioSuite {
}
