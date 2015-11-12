package com.didekin.serviceone.domain;


/**
 * User: pedro
 * Date: 30/03/15
 * Time: 15:14
 */
public final class Municipio implements Comparable<Municipio> {

    private final int mId;  // PK auto-increment.
    private final  short codInProvincia;  // código intra-provincia de un municipio.
    private final  String nombre;
    private final Provincia provincia;

    public Municipio(int mId)
    {
        this.mId = mId;
        codInProvincia = (short) 0;
        nombre = null;
        provincia = null;
    }

    public Municipio(short codInProvincia, Provincia provincia)
    {
        mId = 0;
        this.codInProvincia = codInProvincia;
        this.provincia = provincia;
        nombre = null;
    }

    public Municipio(short codInProvincia, String nombre, Provincia provincia)
    {
        mId = 0;
        this.codInProvincia = codInProvincia;
        this.nombre = nombre;
        this.provincia = provincia;
    }

    public Municipio(int mId, short codInProvincia, String nombre, Provincia provincia)
    {
        this.mId = mId;
        this.codInProvincia = codInProvincia;
        this.nombre = nombre;
        this.provincia = provincia;
    }

    public short getCodInProvincia()
    {
        return codInProvincia;
    }

    public int getmId()
    {
        return mId;
    }

    public String getNombre()
    {
        return nombre;
    }

    public Provincia getProvincia()
    {
        return provincia;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Municipio municipio = (Municipio) o;

        if (mId > 0 && municipio.getmId() > 0) {
            return mId == municipio.getmId();
        }

        return codInProvincia == municipio.codInProvincia && provincia.equals(municipio.provincia);

    }

    @Override
    public int hashCode()
    {
        int result = (int) codInProvincia;
        result = 31 * result + provincia.hashCode();
        return result;
    }

    @Override
    public int compareTo(Municipio o)
    {
        int result;

        if ( (result = provincia.compareTo(o.getProvincia())) != 0) {
            return result;
        }
        if (codInProvincia < o.getCodInProvincia()){
            return -1;
        }
        if (codInProvincia > o.getCodInProvincia()){
            return 1;
        }
        return 0;
    }
}
