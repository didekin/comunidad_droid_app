package com.didekindroid.usuario.dominio;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/08/15
 * Time: 12:06
 */
public class ComunidadTest {

    @Test
    public void testCompareTo_1() throws Exception
    {

        Municipio municipio_1 = new Municipio((short) 23, new Provincia((short) 11));
        Municipio municipio_2 = new Municipio((short) 2, new Provincia((short) 21));

        Comunidad comunidad_1 = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .sufijoNumero("A")
                .municipio(municipio_1).build();

        Comunidad comunidad_2 = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .municipio(municipio_1).build();

        Comunidad comunidad_3 = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .sufijoNumero("A")
                .municipio(municipio_2).build();

        Comunidad comunidad_4 = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .sufijoNumero("b")
                .municipio(municipio_2).build();

        Comunidad comunidad_5 = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreB")
                .numero((short) 2)
                .sufijoNumero("b")
                .municipio(municipio_2).build();

        Comunidad comunidad_6 = new Comunidad.ComunidadBuilder().tipoVia("tipo2")
                .nombreVia("nombreB")
                .numero((short) 2)
                .sufijoNumero("b")
                .municipio(municipio_2).build();

        Comunidad comunidad_2B = new Comunidad.ComunidadBuilder().tipoVia("tipo1")
                .nombreVia("nombreA")
                .numero((short) 2)
                .municipio(municipio_1).build();

        // Sufijo null en comunidad2.
        assertThat(comunidad_1.compareTo(comunidad_2), is(1));
        assertThat(comunidad_2.compareTo(comunidad_1), is(-1));
        // Sufijo null en ambas comunidades.
        assertThat(comunidad_2.compareTo(comunidad_2B), is(0));

        assertThat(comunidad_1.compareTo(comunidad_3), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_4), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_4), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_5), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_6), is(-1));
    }
}