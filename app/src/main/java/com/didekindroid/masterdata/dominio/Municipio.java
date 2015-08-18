package com.didekindroid.masterdata.dominio;

import com.didekindroid.common.dominio.SerialNumbers;

import java.io.Serializable;

/**
 * User: pedro
 * Date: 30/03/15
 * Time: 15:14
 */
/* TODO: a incluir en el .jar común.*/
public class Municipio implements Serializable {

    private static final long serialVersionUID = SerialNumbers.MUNICIPIO.number;

    private int mId;  // PK auto-increment.
    private short codInProvincia;  // código intra-provincia de un municipio.
    private String nombre;
    private Provincia provincia;

    public Municipio()
    {
    }

    public Municipio(Provincia provincia, short mCprovincia)
    {
        this.provincia = provincia;
        codInProvincia = mCprovincia;
    }

    /* En memoria local pueden estar las tablas de comunidad autónoma, provincia y municipio.
            *  En ese caso, mId en local y en servidor serán diferentes. provinciaId y codInProvincia sí coindirán.
            */
    public Municipio(short codInProvincia, String nombre, Provincia provincia)
    {
        this.codInProvincia = codInProvincia;
        this.nombre = nombre;
        this.provincia = provincia;
    }

    public short getCodInProvincia()
    {
        return codInProvincia;
    }

    public void setCodInProvincia(short codInProvincia)
    {
        this.codInProvincia = codInProvincia;
    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int mId)
    {
        this.mId = mId;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public Provincia getProvincia()
    {
        return provincia;
    }

    public void setProvincia(Provincia provincia)
    {
        this.provincia = provincia;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Municipio municipio = (Municipio) o;

        if (codInProvincia != municipio.codInProvincia) return false;
        if (!provincia.equals(municipio.provincia)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = (int) codInProvincia;
        result = 31 * result + provincia.hashCode();
        return result;
    }
}
