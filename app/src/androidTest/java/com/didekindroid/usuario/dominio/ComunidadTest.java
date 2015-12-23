package com.didekindroid.usuario.dominio;

import android.support.test.runner.AndroidJUnit4;

import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/08/15
 * Time: 12:06
 */
@RunWith(AndroidJUnit4.class)
public class ComunidadTest {

    @Test
    public void testCompareTo() throws Exception
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

        assertThat(comunidad_1.compareTo(comunidad_2), is(1));
        assertThat(comunidad_1.compareTo(comunidad_3), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_4), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_4), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_5), is(-1));
        assertThat(comunidad_1.compareTo(comunidad_6), is(-1));
    }
}