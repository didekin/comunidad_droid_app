package com.didekindroid.usuario.dominio;

import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.io.Serializable;

import static com.didekindroid.common.utils.SerialNumber.USUARIO_COMUNIDAD;

/**
 * User: pedro@didekin
 * Date: 09/11/15
 * Time: 18:07
 */
public final class FullUsuarioComuidadIntent implements Serializable {

    private static final long serialVersionUID = USUARIO_COMUNIDAD.number;

    private final FullComunidadIntent comunidadIntent;
    private final long usuarioId;
    private final String userName;
    private final String userAlias;
    private final String portal;
    private final String escalera;
    private final String planta;
    private final String puerta;
    private final String roles;

    public FullUsuarioComuidadIntent(final UsuarioComunidad usuarioComunidad)
    {
        comunidadIntent = new FullComunidadIntent(usuarioComunidad.getComunidad());
        usuarioId = usuarioComunidad.getUsuario().getuId();
        userName = usuarioComunidad.getUsuario().getUserName();
        userAlias = usuarioComunidad.getUsuario().getAlias();
        portal = usuarioComunidad.getPortal();
        escalera = usuarioComunidad.getEscalera();
        planta = usuarioComunidad.getPlanta();
        puerta = usuarioComunidad.getPuerta();
        roles = usuarioComunidad.getRoles();
    }

    public final UsuarioComunidad getUsuarioComunidad()
    {
        return new UsuarioComunidad.UserComuBuilder(
                comunidadIntent.getComunidad(),
                new Usuario.UsuarioBuilder()
                        .uId(usuarioId)
                        .userName(userName)
                        .alias(userAlias)
                        .build())
                .portal(portal)
                .planta(planta)
                .escalera(escalera)
                .puerta(puerta)
                .roles(roles)
                .build();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullUsuarioComuidadIntent that = (FullUsuarioComuidadIntent) o;

        return usuarioId == that.usuarioId && comunidadIntent.equals(that.comunidadIntent);

    }

    @Override
    public int hashCode()
    {
        int result = comunidadIntent.hashCode();
        result = 31 * result + (int) (usuarioId ^ (usuarioId >>> 32));
        return result;
    }
}
