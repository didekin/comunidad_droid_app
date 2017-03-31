package com.didekindroid.incidencia.core;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:38
 */

public class AmbitoIncidValueObj {

    final short _ID;
    final String ambitoStr;

    AmbitoIncidValueObj(short ambitoId, String ambitoStr)
    {
        this._ID = ambitoId;
        this.ambitoStr = ambitoStr;
    }

    @Override
    public String toString()
    {
        return ambitoStr;
    }
}
