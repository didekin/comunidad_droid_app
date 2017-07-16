package com.didekindroid.usuariocomunidad;

import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;
import com.didekinlib.model.usuario.Usuario;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Test;

import static com.didekinlib.model.usuariocomunidad.Rol.ADMINISTRADOR;
import static com.didekinlib.model.usuariocomunidad.Rol.INQUILINO;
import static com.didekinlib.model.usuariocomunidad.Rol.PRESIDENTE;
import static com.didekinlib.model.usuariocomunidad.Rol.PROPIETARIO;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/08/15
 * Time: 13:36
 */
public class UsuarioComunidadTest {

    @Test
    public void testCompareTo() throws Exception
    {

        Usuario usuario = new Usuario.UsuarioBuilder().userName("username").build();
        Municipio municipio_1 = new Municipio((short) 23, new Provincia((short) 11));
        Comunidad comunidad = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .sufijoNumero("A")
                .municipio(municipio_1).build();

        UsuarioComunidad usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portalA")
                .escalera("escaleraA")
                .planta("plantaB")
                .puerta("puertaA")
                .roles(PROPIETARIO.function).build();

        UsuarioComunidad usuarioComunidad_2 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario).build();

        UsuarioComunidad usuarioComunidad_3 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portalB")
                .build();

        UsuarioComunidad usuarioComunidad_4 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .escalera("escaleraB")
                .build();

        UsuarioComunidad usuarioComunidad_5 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .planta("plantaA")
                .build();

        UsuarioComunidad usuarioComunidad_6 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portalA")
                .roles(ADMINISTRADOR.function)
                .build();

        assertThat(usuarioComunidad_1.compareTo(usuarioComunidad_2), is(0));
        assertThat(usuarioComunidad_1.compareTo(usuarioComunidad_3), is(-1));
        assertThat(usuarioComunidad_1.compareTo(usuarioComunidad_4), is(-1));
        assertThat(usuarioComunidad_1.compareTo(usuarioComunidad_5), is(1));
        assertThat(usuarioComunidad_1.compareTo(usuarioComunidad_6), is(0));
    }

    @Test
    public void testHasRoleAdministrador()
    {
        Usuario usuario = new Usuario.UsuarioBuilder().userName("username").build();
        Municipio municipio_1 = new Municipio((short) 23, new Provincia((short) 11));
        Comunidad comunidad = new Comunidad.ComunidadBuilder()
                .tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .municipio(municipio_1).build();

        UsuarioComunidad usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portalA")
                .roles(INQUILINO.function)
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(INQUILINO.function.concat(",").concat(ADMINISTRADOR.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(true));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(PROPIETARIO.function)
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(INQUILINO.function.concat(",").concat(PROPIETARIO.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(INQUILINO.function.concat(",")
                        .concat(PRESIDENTE.function).concat(",")
                        .concat(ADMINISTRADOR.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(true));
    }
}