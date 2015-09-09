package com.didekindroid.usuario.dominio;

import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;

/**
 * User: pedro@didekin
 * Date: 01/09/15
 * Time: 11:32
 */
public final class DomainDataUtils {

    private DomainDataUtils()
    {
    }

    public static final Usuario USER_PACO = new Usuario.UsuarioBuilder()
            .userName("paco@paco.com")
            .alias("paco")
            .password("pwswd00")
            .prefixTf((short) 34)
            .numeroTf(615221110)
            .build();

    public static final Usuario USER_JUAN_with_TF = new Usuario.UsuarioBuilder()
            .userName("juan@juan.com")
            .alias("juan")
            .password("pswd01")
            .prefixTf((short) 34)
            .numeroTf(600121233)
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("juan@juan.com")
            .alias("juan")
            .password("password11")
            .build();

    public static final Comunidad COMU_LA_FUENTE = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de la Fuente")
            .numero((short) 11)
            .municipio(new Municipio((short) 66, new Provincia((short) 3)))
            .build();

    public static final Comunidad COMU_LA_PLAZUELA_10bis = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 10)
            .sufijoNumero("bis")
            .municipio(new Municipio((short) 52, new Provincia((short) 2)))
            .build();

    public static final Comunidad COMU_LA_PLAZUELA_10 = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 10)
            .municipio(new Municipio((short) 52, new Provincia((short) 2)))
            .build();

    public static final Comunidad COMU_EL_ESCORIAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de El Escorial")
            .numero((short) 2)
            .municipio(new Municipio((short) 27, new Provincia((short) 4)))
            .build();

    public static final Comunidad COMU_REAL = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Real")
            .numero((short) 5)
            .sufijoNumero("Bis")
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
            .build();

    public static final Comunidad COMU_OTRA = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("Otra")
            .numero((short) 3)
            .municipio(new Municipio((short) 14, new Provincia((short) 45)))
            .build();

    public static UsuarioComunidad makeUsuarioComunidad(Comunidad comunidad, Usuario usuario, String portal, String escalera,
                                                        String planta,
                                                        String puerta,
                                                        String roles)
    {
        return new UsuarioComunidad.UserComuBuilder(comunidad, usuario)
                .portal(portal)
                .escalera(escalera)
                .planta(planta)
                .puerta(puerta)
                .roles(roles).build();
    }
}
