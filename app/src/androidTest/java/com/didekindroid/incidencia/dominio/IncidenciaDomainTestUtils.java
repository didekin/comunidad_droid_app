package com.didekindroid.incidencia.dominio;

import com.didekin.incidservice.domain.AmbitoIncidencia;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.serviceone.domain.Comunidad;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidenciaDomainTestUtils {

    private IncidenciaDomainTestUtils()
    {
    }

    public static Incidencia doIncidencia(String descripcion, long comunidadId, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .comunidad(new Comunidad.ComunidadBuilder().c_id(comunidadId).build())
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }
}
