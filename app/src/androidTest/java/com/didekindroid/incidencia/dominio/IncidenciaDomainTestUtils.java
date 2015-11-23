package com.didekindroid.incidencia.dominio;

import com.didekin.incidservice.domain.AmbitoIncidencia;
import com.didekin.incidservice.domain.Incidencia;

/**
 * User: pedro@didekin
 * Date: 23/11/15
 * Time: 12:37
 */
public final class IncidenciaDomainTestUtils {

    private IncidenciaDomainTestUtils()
    {
    }

    public static Incidencia doIncidencia(String descripcion, short ambitoId)
    {
        return new Incidencia.IncidenciaBuilder()
                .descripcion(descripcion)
                .ambitoIncid(new AmbitoIncidencia(ambitoId))
                .build();
    }
}
