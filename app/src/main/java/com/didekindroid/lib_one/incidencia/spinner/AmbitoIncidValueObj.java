package com.didekindroid.lib_one.incidencia.spinner;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:38
 */

public class AmbitoIncidValueObj implements Serializable {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    public final short id;
    public final String ambitoStr;

    public AmbitoIncidValueObj(short ambitoId, String ambitoStr)
    {
        id = ambitoId;
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
