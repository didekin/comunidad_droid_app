package com.didekin.serviceone.domain;

import com.didekin.common.domain.DataPatternsIf;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * User: pedro@didekin
 * Date: 10/06/15
 * Time: 10:13
 */
public enum UserDataPatterns implements DataPatternsIf {

    MUNICIPIO_DESC("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{2,100}"),
    PROVINCIA_DESC("[a-zA-ZñÑáéíóúüÜ[\\s]]{2,100}"),
    MUNICIPIO_COD("[0-9]{1,3}"),
    PROVINCIA_COD("[0-9]{1,3}"),

    /*  COMUNIDAD */
    TIPO_VIA("[a-zA-ZñÑáéíóúüÜ]{2,25}"),
    NOMBRE_VIA("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{2,150}"),
    SUFIJO_NUMERO("[a-zA-ZñÑáéíóúüÜ]{1,10}"),
    NOMBRE_COMUNIDAD("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{4,100}"),

    /* USUARIO */
    EMAIL("[\\w\\._\\-]{1,48}@[\\w\\-_]{1,40}\\.[\\w&&[^0-9]]{1,10}"),
    PASSWORD("[0-9a-zA-Z_ñÑáéíóúüÜ]{6,60}"),
    ALIAS("[0-9a-zA-Z_ñÑáéíóúüÜ]{6,30}"),

    /* USUARIO_COMUNIDAD */
    PORTAL("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    ESCALERA("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    PLANTA("[\\w_ñÑáéíóúüÜ\\.\\-\\s]{1,10}"),
    PUERTA("[\\w_ñÑáéíóúüÜ\\.\\-]{1,10}"),

    /* GENERIC */
    LINE_BREAK("\n"),
    ;

    private final Pattern pattern;
    private final String regexp;

    UserDataPatterns(String patternString)
    {
        pattern = compile(patternString, UNICODE_CASE | CASE_INSENSITIVE);
        regexp = patternString;
    }

    @Override
    public boolean isPatternOk(String fieldToCheck)
    {
        return pattern.matcher(fieldToCheck).matches();
    }

    @Override
    public Pattern getPattern()
    {
        return pattern;
    }

    @Override
    public String getRegexp()
    {
        return regexp;
    }
}
