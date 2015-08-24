package com.didekindroid.usuario.dominio;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * User: pedro@didekin
 * Date: 18/05/15
 * Time: 13:16
 */
public enum UserPatterns {

    /*  COMUNIDAD */
    TIPO_VIA("[a-zA-ZñÑáéíóúüÜ]{2,25}"),
    NOMBRE_VIA("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{2,150}"),
    SUFIJO_NUMERO("[a-zA-ZñÑáéíóúüÜ]{1,10}"),
    NOMBRE_COMUNIDAD("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{4,100}"),

    /* USUARIO */
    EMAIL("[\\w\\._\\-]{1,48}@[\\w\\-_]{1,40}\\.[\\w&&[^0-9]]{1,10}"),
    PASSWORD("[0-9a-zA-Z_ñÑáéíóúüÜ]{6,60}"),
    ALIAS("[0-9a-zA-Z_ñÑáéíóúüÜ]{6,60}"),
    PREFIX("[0-9]{1,4}"),
    TELEFONO("[0-9]{6,15}"),


    /* USUARIO_COMUNIDAD */
    PORTAL("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    ESCALERA("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    PLANTA("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    PUERTA("[\\w_ñÑáéíóúüÜ\\.\\-]{1,10}"),;

    public final Pattern pattern;

    UserPatterns(String patternString)
    {
        pattern = compile(patternString, UNICODE_CASE | CASE_INSENSITIVE);
    }
}