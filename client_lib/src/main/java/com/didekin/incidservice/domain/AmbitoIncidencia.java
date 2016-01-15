package com.didekin.incidservice.domain;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 12/11/15
 * Time: 18:20
 */
public class AmbitoIncidencia implements Serializable {

    private final short ambitoId;
    private final String descripcion;

    public AmbitoIncidencia(short ambitoId)
    {
        this.ambitoId = ambitoId;
        descripcion = null;
    }

    public AmbitoIncidencia(short ambitoId, String descripcion)
    {
        this.ambitoId = ambitoId;
        this.descripcion = descripcion;
    }

    public short getAmbitoId()
    {
        return ambitoId;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AmbitoIncidencia that = (AmbitoIncidencia) o;

        return ambitoId == that.ambitoId;

    }

    @Override
    public int hashCode()
    {
        return (int) ambitoId;
    }
}
