package com.didekinaar.usuario;

import com.didekin.usuario.Usuario;

import org.junit.Test;

/**
 * User: pedro@didekin
 * Date: 31/08/15
 * Time: 14:23
 */
public class UsuarioTest {

    @Test(expected = IllegalStateException.class)
    public void testBuild_1()
    {
        new Usuario.UsuarioBuilder().build();
    }

}