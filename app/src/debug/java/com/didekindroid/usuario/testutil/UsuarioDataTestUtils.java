package com.didekindroid.usuario.testutil;

import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDaoRemote;

/**
 * User: pedro
 * Date: 21/07/15
 * Time: 11:19
 */

public final class UsuarioDataTestUtils {

    public static final Usuario USER_DROID = new Usuario.UsuarioBuilder()
            .userName("didekindroid@didekin.es")
            .alias("didekindroid")
            .password("psw_droid")
            .build();

    //  ======================================= CREATING METHODS  ==========================================
    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("juan@didekin.es")
            .alias("alias_juan")
            .password("psw_juan")
            .build();
    public static final Usuario USER_JUAN2 = new Usuario.UsuarioBuilder()
            .userName("juan2@didekin.es")
            .alias("alias_juan2")
            .password("pswd01")
            .build();
    public static final Usuario USER_PEPE = new Usuario.UsuarioBuilder()
            .userName("pedro@didekin.es")
            .alias("alias_pepe")
            .password("psw_pepe")
            .build();

    private UsuarioDataTestUtils()
    {
    }

    //  ======================================= CLEANING METHODS  ==========================================

    public static void cleanOneUser(Usuario usuario) throws UiException
    {
        updateSecurityData(usuario.getUserName(), usuario.getPassword());
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();
    }

    private static void cleanTwoUsers(Usuario usuarioOne, Usuario usuarioTwo) throws UiException
    {
        cleanOneUser(usuarioOne);
        cleanOneUser(usuarioTwo);
    }

    public static void cleanWithTkhandler()
    {
        TKhandler.cleanIdentityCache();
        TKhandler.updateIsRegistered(false);
    }

    public static void cleanOptions(CleanUserEnum whatClean) throws UiException
    {
        switch (whatClean) {
            case CLEAN_TK_HANDLER:
                cleanWithTkhandler();
                break;
            case CLEAN_JUAN:
                cleanOneUser(USER_JUAN);
                break;
            case CLEAN_PEPE:
                cleanOneUser(USER_PEPE);
                break;
            case CLEAN_JUAN2:
                cleanOneUser(USER_JUAN2);
                break;
            case CLEAN_DROID:
                cleanOneUser(USER_DROID);
                break;
            case CLEAN_JUAN_AND_PEPE:
                cleanTwoUsers(USER_JUAN, USER_PEPE);
                break;
            case CLEAN_JUAN2_AND_PEPE:
                cleanTwoUsers(USER_JUAN2, USER_PEPE);
                break;
            case CLEAN_NOTHING:
                break;
            default:
                throw new IllegalStateException("Wrong cleanUp");
        }
    }

    public enum CleanUserEnum {

        CLEAN_JUAN,
        CLEAN_PEPE,
        CLEAN_JUAN_AND_PEPE,
        CLEAN_JUAN2_AND_PEPE,
        CLEAN_TK_HANDLER,
        CLEAN_NOTHING,
        CLEAN_JUAN2,
        CLEAN_DROID,
        ;
    }
}


