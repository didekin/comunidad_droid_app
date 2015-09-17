package com.didekindroid.usuario.dominio;

import com.didekin.serviceone.domain.*;

import java.util.ArrayList;
import java.util.List;

import static com.didekindroid.usuario.activity.utils.RolCheckBox.*;

/**
 * User: pedro@didekin
 * Date: 01/09/15
 * Time: 11:32
 */
public final class DomainDataUtils {

    private DomainDataUtils()
    {
    }

    public static final Usuario USER_PEPE = new Usuario.UsuarioBuilder()
            .userName("pepe@pepe.org")
            .alias("pepe")
            .password("psw_pepe")
            .build();

    public static final Usuario USER_JUAN2 = new Usuario.UsuarioBuilder()
            .userName("juan@juan.com")
            .alias("alias_juan")
            .password("pswd01")
            .build();

    public static final Usuario USER_JUAN = new Usuario.UsuarioBuilder()
            .userName("juan@juan.us")
            .alias("juan")
            .password("psw_juan")
            .build();

    public static final Comunidad COMU_LA_FUENTE = new Comunidad.ComunidadBuilder()
            .tipoVia("Calle")
            .nombreVia("de la Fuente")
            .numero((short) 11)
            .municipio(new Municipio((short) 66, new Provincia((short) 3)))
            .build();

    public static final Comunidad COMU_LA_PLAZUELA_5 = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 5)
            .municipio(new Municipio((short) 2, new Provincia((short) 27)))
            .build();

    public static final Comunidad COMU_LA_PLAZUELA_10bis = new Comunidad.ComunidadBuilder()
            .tipoVia("Ronda")
            .nombreVia("de la Plazuela")
            .numero((short) 10)
            .sufijoNumero("bis")
            .municipio(new Municipio((short) 52, new Provincia((short) 2)))
            .build();

    public static final Comunidad COMU_TRAV_PLAZUELA_11 = new Comunidad.ComunidadBuilder()
            .tipoVia("Travesía")
            .nombreVia("de la Plazuela")
            .numero((short) 11)
            .municipio(new Municipio((short) 13, new Provincia((short) 3)))
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

    public static final UsuarioComunidad COMU_REAL_JUAN = makeUsuarioComunidad(COMU_REAL, USER_JUAN, "portal", "esc",
            "plantaX", "door12", PROPIETARIO.function);

    public static final UsuarioComunidad COMU_PLAZUELA5_JUAN = makeUsuarioComunidad(COMU_LA_PLAZUELA_5, USER_JUAN, null,
            null, "planta3", "doorA", ADMINISTRADOR.function);

    public static final UsuarioComunidad COMU_REAL_PEPE = makeUsuarioComunidad(COMU_REAL, USER_PEPE, "portal",
            "esc", "plantaY", "door21", PROPIETARIO.function);

    public static final UsuarioComunidad COMU_TRAV_PLAZUELA_PEPE = makeUsuarioComunidad(COMU_TRAV_PLAZUELA_11, USER_PEPE,
            "portalA", null, "planta2", null, INQUILINO.function);


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

    public static Usuario makeUsuario(String userName,String alias,String password,short prefixTf,int numeroTf)
    {
        return new Usuario.UsuarioBuilder()
                .userName(userName)
                .alias(alias)
                .password(password)
                .build();
    }

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

    public static List<UsuarioComunidad> makeListTwoUserComu()
    {
        List<UsuarioComunidad> userComuList = new ArrayList<UsuarioComunidad>(2);
        userComuList.add(COMU_REAL_JUAN);
        userComuList.add(COMU_PLAZUELA5_JUAN);
        return userComuList;
    }
}
