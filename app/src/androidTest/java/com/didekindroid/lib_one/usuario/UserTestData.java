package com.didekindroid.lib_one.usuario;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.IOException;

import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.security.SecurityTestUtils.updateSecurityData;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 14:04
 */

public final class UserTestData {

    public static final Usuario user_crodrigo = new Usuario.UsuarioBuilder()
            .userName("crodrigo@didekin.es")
            .alias("alias_juan")
            .password("psw_juan")
            .build();

    public static final Usuario USER_DROID = new Usuario.UsuarioBuilder()
            .userName("didekindroid@didekin.es")
            .alias("didekindroid")
            .password("psw_droid")
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("crodrigo@didekin.es")
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

    // Municipio: Algueña  Provincia: Alicante/Alacant
    public static final Comunidad comu_real = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Real")
            .numero((short) 5)
            .sufijoNumero("Bis")
            .municipio(new Municipio((short) 13, "Algueña", new Provincia(new ComunidadAutonoma((short) 10), (short) 3, "Alicante/Alacant")))
            .build();

    public static final UsuarioComunidad comu_real_rodrigo = new UsuarioComunidad.UserComuBuilder(comu_real, user_crodrigo)
            .portal("portal")
            .escalera("esc")
            .planta("plantaX")
            .puerta("door12")
            .roles(PROPIETARIO.function).build();

    private UserTestData()
    {
    }

    // =========================  Registet methods =========================

    public static void regUserComuWithTkCache(UsuarioComunidad userComuIn) throws java.io.IOException, UiException
    {
        //Inserta userComu, comunidad y usuariocomunidad.
        assertThat(usuarioMockDao.regComuAndUserAndUserComu(userComuIn).execute().body(), is(true));
        updateSecurityData(userComuIn.getUsuario().getUserName(), userComuIn.getUsuario().getPassword());
    }

    public static Usuario regGetUserComu(UsuarioComunidad userComuIn) throws java.io.IOException, UiException
    {
        //Inserta userComu, comunidad y usuariocomunidad.
        regUserComuWithTkCache(userComuIn);
        return usuarioDaoRemote.getUserData();
    }

    // =========================  Cleaning methods =========================

    public static void cleanOneUser(Usuario usuario) throws UiException
    {
        updateSecurityData(usuario.getUserName(), usuario.getPassword());
        try {
            usuarioMockDao.deleteUser(usuario.getUserName()).execute().body();
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
        cleanWithTkhandler();
    }

    public static void cleanTwoUsers(Usuario usuarioOne, Usuario usuarioTwo) throws UiException
    {
        cleanOneUser(usuarioOne);
        cleanOneUser(usuarioTwo);
    }

    public static void cleanWithTkhandler()
    {
        secInitializer.get().getTkCacher().cleanIdentityCache();
        secInitializer.get().getTkCacher().updateIsRegistered(false);
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
            case CLEAN_RODRIGO:
                cleanOneUser(user_crodrigo);
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

    // =========================  Inner classes =========================

    public enum CleanUserEnum {

        CLEAN_JUAN,
        CLEAN_PEPE,
        CLEAN_JUAN_AND_PEPE,
        CLEAN_JUAN2_AND_PEPE,
        CLEAN_TK_HANDLER,
        CLEAN_NOTHING,
        CLEAN_JUAN2,
        CLEAN_DROID,
        CLEAN_RODRIGO,;
    }
}
