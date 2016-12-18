package com.didekinaar;

import com.didekinaar.usuario.AarUsuarioSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:01
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AarCommonSuite.class,
        AarUsuarioSuite.class,
})
public class AarSuite {
}
