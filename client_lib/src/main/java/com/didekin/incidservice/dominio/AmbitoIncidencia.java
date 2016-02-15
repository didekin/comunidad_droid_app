package com.didekin.incidservice.dominio;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:20
 */
public class AmbitoIncidencia implements Serializable {

    private final short ambitoId;

    public AmbitoIncidencia(short ambitoId)
    {
        this.ambitoId = ambitoId;
    }

    public short getAmbitoId()
    {
        return ambitoId;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof AmbitoIncidencia)) return false;

        AmbitoIncidencia that = (AmbitoIncidencia) o;

        return ambitoId == that.ambitoId;

    }

    @Override
    public int hashCode()
    {
        return (int) ambitoId;
    }
}
