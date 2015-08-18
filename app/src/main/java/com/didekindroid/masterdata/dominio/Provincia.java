package com.didekindroid.masterdata.dominio;

import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 10/06/15
 * Time: 13:08
 */
public class Provincia implements Serializable {

    private static final long serialVersionUID = SerialNumbers.PROVINCIA.number;

    private short provinciaId; // Es una PK fija, no un campo auto-increment.
    private java.lang.String nombre;

    public Provincia()
    {
    }

    public Provincia(short provinciaId)
    {
        this.provinciaId = provinciaId;
    }

    public Provincia(short provinciaId, String nombre)
    {
        this.provinciaId = provinciaId;
        this.nombre = nombre;
    }

    public short getProvinciaId()
    {
        return provinciaId;
    }

    public java.lang.String getNombre()
    {
        return nombre;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provincia provincia = (Provincia) o;

        if (provinciaId != provincia.provinciaId) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return (int) provinciaId;
    }
}
