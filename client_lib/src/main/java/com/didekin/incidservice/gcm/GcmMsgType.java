package com.didekin.incidservice.gcm;

/**
 * User: pedro@didekin
 * Date: 03/12/15
 * Time: 12:02
 */
@SuppressWarnings("unused")
public enum GcmMsgType {

    incidencia,
    ;


    @Override
    public String toString()
    {
        return incidencia.name();
    }
}
