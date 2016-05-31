package com.didekindroid.usuario.dominio;

import com.didekin.usuario.dominio.Rol;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;

import org.junit.Test;

import static com.didekin.usuario.dominio.Rol.ADMINISTRADOR;
import static com.didekin.usuario.dominio.Rol.PROPIETARIO;
import static org.hamcrest.Matchers.is;
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
                .roles(Rol.INQUILINO.function)
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(Rol.INQUILINO.function.concat(",").concat(Rol.ADMINISTRADOR.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(true));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(Rol.PROPIETARIO.function)
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(Rol.INQUILINO.function.concat(",").concat(Rol.PROPIETARIO.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(false));

        usuarioComunidad_1 = new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal("portal B")
                .roles(Rol.INQUILINO.function.concat(",")
                        .concat(Rol.PRESIDENTE.function).concat(",")
                        .concat(Rol.ADMINISTRADOR.function))
                .build();
        assertThat(usuarioComunidad_1.hasAdministradorAuthority(), is(true));
    }
}