package com.didekindroid.usuario;


import com.didekinlib.model.usuario.Usuario;

import org.junit.Test;

/**
 * User: pedro@didekin
 * Date: 31/08/15
 * Time: 14:23
 */
public class UsuarioTest {

    /**
     *  if usuario.uId == 0 && usuario.userName == null
     *  throws IllegalStateException.
     */
    @Test(expected = IllegalStateException.class)
    public void testBuild_1()
    {
        new Usuario.UsuarioBuilder().build();
    }

}