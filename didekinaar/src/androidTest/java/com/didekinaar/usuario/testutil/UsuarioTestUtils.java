package com.didekinaar.usuario.testutil;

import com.didekin.usuario.Usuario;

/**
 * User: pedro
 * Date: 21/07/15
 * Time: 11:19
 */

public final class UsuarioTestUtils {

    private UsuarioTestUtils()
    {
    }

    public static final Usuario USER_DROID = new Usuario.UsuarioBuilder()
            .userName("didekindroid@didekin.es")
            .alias("didekindroid")
            .password("psw_droid")
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("juan@juan.us")
            .alias("alias_juan")
            .password("psw_juan")
            .build();

    public static final Usuario USER_JUAN2 = new Usuario.UsuarioBuilder()
            .userName("juan@juan.com")
            .alias("alias_juan")
            .password("pswd01")
            .build();

    public static final Usuario USER_PEPE = new Usuario.UsuarioBuilder()
            .userName("pepe@pepe.org")
            .alias("pepe")
            .password("psw_pepe")
            .build();

    //  ======================================= ENTITIES METHODS  ==========================================

    public static Usuario makeUsuario(String userName, String alias, String password)
    {
        return new Usuario.UsuarioBuilder()
                .userName(userName)
                .alias(alias)
                .password(password)
                .build();
    }
}


