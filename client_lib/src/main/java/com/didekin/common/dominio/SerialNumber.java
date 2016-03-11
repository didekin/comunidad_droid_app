package com.didekin.common.dominio;

/**
 * User: pedro@didekin
 * Date: 09/11/15
 * Time: 16:25
 */
public enum SerialNumber {

    AMBITO_INCIDENCIA(1L),
    COMUNIDAD(21L),
    COMUNIDAD_AUTONOMA(3L),
    INCIDENCIA(41L),
    INCID_IMPORTANCIA(42L),
    INCID_RESOLUCION(43L),
    INCID_RESOLUCION_AVANCE(44L),
    INCIDENCIA_USER(45L),
    MUNICIPIO(5L),
    PROVINCIA(6L),
    USUARIO_COMUNIDAD(71L),
    USUARIO(72L),
    ;

    public final long number;

    SerialNumber(long number)
    {
        this.number = number;
    }
}
