package com.didekindroid.incidencia.core;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:38
 */

public class AmbitoIncidValueObj {

    final short _ID;
    final String ambitoStr;

    public AmbitoIncidValueObj(short ambitoId, String ambitoStr)
    {
        _ID = ambitoId;
        this.ambitoStr = ambitoStr;
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
