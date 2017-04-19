package com.didekindroid.incidencia.core;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:38
 */

public class AmbitoIncidValueObj implements Serializable {

    final short _ID;
    final String ambitoStr;

    public AmbitoIncidValueObj(short ambitoId, String ambitoStr)
    {
        _ID = ambitoId;
        this.ambitoStr = ambitoStr;
    }

    public short get_ID()
    {
        return _ID;
    }

    @Override
    public String toString()
    {
        return ambitoStr;
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof AmbitoIncidValueObj && toString().equals(obj.toString());
    }
}
