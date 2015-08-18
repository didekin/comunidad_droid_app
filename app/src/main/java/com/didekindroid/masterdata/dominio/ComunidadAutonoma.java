package com.didekindroid.masterdata.dominio;

import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 16/06/15
 * Time: 15:09
 */
public class ComunidadAutonoma implements Serializable {

    private static final long serialVersionUID = SerialNumbers.COMUNIDAD_AUTONOMA.number;

    private final short cuId;
    private final String nombre;

    public ComunidadAutonoma(short cuId, String nombre)
    {
        this.cuId = cuId;
        this.nombre = nombre;
    }

    public short getCuId()
    {
        return cuId;
    }

    public String getNombre()
    {
        return nombre;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComunidadAutonoma that = (ComunidadAutonoma) o;

        if (cuId != that.cuId) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) cuId;
    }
}
