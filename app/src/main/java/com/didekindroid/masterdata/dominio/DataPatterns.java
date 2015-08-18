package com.didekindroid.masterdata.dominio;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * User: pedro@didekin
 * Date: 10/06/15
 * Time: 10:13
 */
public enum DataPatterns {

    MUNICIPIO_DESC("[0-9a-zA-ZñÑáéíóúüÜ[\\s]]{2,100}"),
    PROVINCIA_DESC("[a-zA-ZñÑáéíóúüÜ[\\s]]{2,100}"),
    MUNICIPIO_COD("[0-9]{1,3}"),
    PROVINCIA_COD("[0-9]{1,3}")
    ;

    public final Pattern pattern;

    DataPatterns(String patternString)
    {
        pattern = compile(patternString, UNICODE_CASE | CASE_INSENSITIVE);
    }

}
