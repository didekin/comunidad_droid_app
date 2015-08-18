package com.didekindroid.common.dominio;

/**
 * User: pedro@didekin
 * Date: 21/05/15
 * Time: 10:34
 */
public enum SerialNumbers {

    COMUNIDAD_BEAN(1L),
    COMUNIDAD(2L),
    USUARIO_COMUNIDAD_BEAN(3L),
    USUARIO_COMUNIDAD(4L),
    USUARIO_BEAN(5L),
    USUARIO(6L),
    MUNICIPIO(7L),
    PROVINCIA(8L),
    COMUNIDAD_AUTONOMA(9L);

    public final long number;

    SerialNumbers(long number)
    {
        this.number = number;
    }
}
