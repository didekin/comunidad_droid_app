package com.didekindroid.lib_one.comunidad.spinner;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 10:13
 */
@SuppressWarnings("WeakerAccess")
public class TipoViaValueObj implements Serializable {

    private final int pk;
    private final String tipoViaDesc;

    public TipoViaValueObj(int pk, String tipoViaDesc)
    {
        this.pk = pk;
        this.tipoViaDesc = tipoViaDesc;
    }

    public TipoViaValueObj(String tipoVia)
    {
        this(0, tipoVia);
    }

    public int getPk()
    {
        return pk;
    }

    public String getTipoViaDesc()
    {
        return tipoViaDesc;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj)
    {
        return TipoViaValueObj.class.cast(obj).getPk() == pk
                && TipoViaValueObj.class.cast(obj).getTipoViaDesc().equals(tipoViaDesc);
    }

    @Override
    public int hashCode()
    {
        return 31 * pk + (tipoViaDesc != null ? tipoViaDesc.hashCode() : 0);
    }

    @Override
    public String toString()
    {
        return tipoViaDesc;
    }
}
