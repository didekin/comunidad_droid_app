package com.didekindroid.comunidad;

import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.Municipio;
import com.didekin.comunidad.Provincia;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 11:49
 */

public final class ComuTestUtil {

    private ComuTestUtil()
    {
    }

    // Municipio: Benizalón  Provincia: Almería
    public static final Comunidad COMU_EL_ESCORIAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de El Escorial")
            .numero((short) 2)
            .municipio(new Municipio((short) 27, new Provincia((short) 4)))
            .build();
    // Municipio: Elda   Provincia: Alicante/Alacant
    public static final Comunidad COMU_LA_FUENTE = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de la Fuente")
            .numero((short) 11)
            .municipio(new Municipio((short) 66, new Provincia((short) 3)))
            .build();
    // Municipio: Alfoz   Provincia: Lugo
    public static final Comunidad COMU_LA_PLAZUELA_5 = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 5)
            .municipio(new Municipio((short) 2, new Provincia((short) 27)))
            .build();
    // Municipio:  Algueña   Provincia: Alicante/Alacant
    public static final Comunidad COMU_TRAV_PLAZUELA_11 = new Comunidad.ComunidadBuilder()
            .tipoVia("Travesía")
            .nombreVia("de la Plazuela")
            .numero((short) 11)
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
            .build();
    // Municipio: Algueña  Provincia: Alicante/Alacant
    public static final Comunidad COMU_REAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Real")
            .numero((short) 5)
            .sufijoNumero("Bis")
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
            .build();

    public static Comunidad makeComunidad(String tipoVia, String nombreVia, short numero, String sufijoNumero,
                                          Municipio municipio)
    {
        return new Comunidad.ComunidadBuilder().tipoVia(tipoVia)
                .nombreVia(nombreVia)
                .numero(numero)
                .sufijoNumero(sufijoNumero)
                .municipio(municipio)
                .build();
    }
}
