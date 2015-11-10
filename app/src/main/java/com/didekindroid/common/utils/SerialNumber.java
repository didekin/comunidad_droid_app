package com.didekindroid.common.utils;

/**
 * User: pedro@didekin
 * Date: 09/11/15
 * Time: 16:25
 */
public enum SerialNumber {

    COMUNIDAD_INTENT(11L),
    COMUNIDAD_FULL_INTENT(12L),
    USUARIO_COMUNIDAD(13L),
    ;

    public final long number;

    SerialNumber(long number)
    {
        this.number = number;
    }
}
