package com.didekin.serviceone.domain;


/**
 * User: pedro@didekin
 * Date: 10/06/15
 * Time: 13:08
 */
public final class Provincia implements Comparable<Provincia>{

    private final short provinciaId; // Es una PK fija, no un campo auto-increment.
    private final String nombre;
    private final ComunidadAutonoma comunidadAutonoma;

    public Provincia(short provinciaId)
    {
        this.provinciaId = provinciaId;
        nombre = null;
        comunidadAutonoma = null;
    }

    public Provincia(short provinciaId, String nombre)
    {
        this.nombre = nombre;
        this.provinciaId = provinciaId;
        comunidadAutonoma = null;
    }

    public Provincia(ComunidadAutonoma comunidadAutonoma, short provinciaId, String nombre)
    {
        this.comunidadAutonoma = comunidadAutonoma;
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

    @SuppressWarnings("unused")
    public ComunidadAutonoma getComunidadAutonoma()
    {
        return comunidadAutonoma;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o){
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Provincia provincia = (Provincia) o;

        return provinciaId == provincia.provinciaId;
    }

    @Override
    public int hashCode()
    {
        return (int) provinciaId;
    }

    @Override
    public int compareTo(Provincia o)
    {
        if (provinciaId < o.getProvinciaId()){
            return -1;
        } else if(provinciaId > o.getProvinciaId()){
            return 1;
        } else {
            return 0;
        }
    }
}
