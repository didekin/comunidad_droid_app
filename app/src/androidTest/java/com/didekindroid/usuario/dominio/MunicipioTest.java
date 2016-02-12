package com.didekindroid.usuario.dominio;

import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/08/15
 * Time: 11:46
 */
public class MunicipioTest {

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void testCompareTo() throws Exception
    {
        Municipio municipio_1 = new Municipio((short) 23,new Provincia((short)11));
        Municipio municipio_2 = new Municipio((short) 2,new Provincia((short)11));
        Municipio municipio_3 = new Municipio((short) 11,new Provincia((short)9));

        assertThat((municipio_1.equals(municipio_1)) == (municipio_1.compareTo(municipio_1) == 0), is(true));
        assertThat(municipio_1.compareTo(municipio_2), is(1));
        assertThat(municipio_2.compareTo(municipio_3), is(1));
        assertThat(municipio_1.compareTo(municipio_3), is(1));
    }
}