package com.didekin.incidservice.domain;

import com.didekin.common.domain.DataPatternsIf;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;
import static java.util.regex.Pattern.compile;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 19:26
 */
public enum IncidDataPatterns implements DataPatternsIf {

    INCID_DESC("[\\w_ñÑáéíóúüÜÁÉÍÓÚ%&ºª@#,:;¿\\?¡\\!\\(\\)\\.\\-\\s]{2,300}"),
    INCID_COMMENT_DESC("[\\w_ñÑáéíóúüÜÁÉÍÓÚ%&ºª@#,:;¿\\?¡\\!\\(\\)\\.\\-\\s]{2,250}"),
    ;

    private final Pattern pattern;
    private final String regexp;

    IncidDataPatterns(String patternString)
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
    public String getRegexp()
    {
        return regexp;
    }
}
