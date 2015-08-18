package com.didekindroid.usuario.comunidad.dominio;

import com.didekindroid.common.dominio.SerialNumbers;
import com.didekindroid.masterdata.dominio.Municipio;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 09/06/15
 * Time: 20:17
 */
public class Comunidad implements Serializable {

    private static final long serialVersionUID = SerialNumbers.COMUNIDAD.number;

    private long c_Id;
    private String tipoVia;  // not null.
    private String nombreVia;    // not null.
    private short numero;  // not null.
    private String sufijoNumero;
    private Municipio municipio;  // not null.
    private Timestamp fechaAlta;
    private Timestamp fechaMod;

    public Comunidad()
    {
    }

    public Comunidad(String tipoVia, String nombreVia, short numero, String sufijoNumero, Municipio municipio)
    {
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        this.numero = numero;
        this.sufijoNumero = sufijoNumero;
        this.municipio = municipio;
    }

    public Comunidad(long c_id, String tipoVia, String nombreVia, short numero,
                     String sufijoNumero, Municipio municipio)
    {
        this(tipoVia, nombreVia, numero, sufijoNumero, municipio);
        this.c_Id = c_id;
    }

    public Comunidad(String tipoVia, String nombreVia, String sufijoNumero, Municipio municipio)
    {
        this.tipoVia = tipoVia;
        this.nombreVia = nombreVia;
        this.sufijoNumero = sufijoNumero;
        this.municipio = municipio;
    }


    public long getC_Id()
    {
        return c_Id;
    }

    public String getNombreVia()
    {
        return nombreVia;
    }

    public String getSufijoNumero()
    {
        return sufijoNumero;
    }

    public Timestamp getFechaAlta()
    {
        return fechaAlta;
    }

    public Timestamp getFechaMod()
    {
        return fechaMod;
    }

    public Municipio getMunicipio()
    {
        return municipio;
    }

    public short getNumero()
    {
        return numero;
    }

    public String getTipoVia()
    {
        return tipoVia;
    }

    public void setC_Id(long c_Id)
    {
        this.c_Id = c_Id;
    }

    public void setNombreVia(String nombreVia)
    {
        this.nombreVia = nombreVia;
    }

    public void setSufijoNumero(String sufijoNumero)
    {
        this.sufijoNumero = sufijoNumero;
    }

    public void setFechaAlta(Timestamp fechaAlta)
    {
        this.fechaAlta = fechaAlta;
    }

    public void setFechaMod(Timestamp fechaMod)
    {
        this.fechaMod = fechaMod;
    }

    public void setMunicipio(Municipio municipio)
    {
        this.municipio = municipio;
    }

    public void setNumero(short numero)
    {
        this.numero = numero;
    }

    public void setTipoVia(String tipoVia)
    {
        this.tipoVia = tipoVia;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comunidad comunidad = (Comunidad) o;

        if (!tipoVia.equals(comunidad.tipoVia)) return false;
        if (!nombreVia.equals(comunidad.nombreVia)) return false;
        if (numero != comunidad.numero) return false;
        if (sufijoNumero != null ? !sufijoNumero.equals(comunidad.sufijoNumero) : comunidad.sufijoNumero != null)
            return false;
        if (!municipio.equals(comunidad.municipio)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = tipoVia.hashCode();
        result = 31 * result + nombreVia.hashCode();
        result = 31 * result + numero;
        result = 31 * result + (sufijoNumero != null ? sufijoNumero.hashCode() : 0);
        result = 31 * result + municipio.hashCode();
        return result;
    }
}

