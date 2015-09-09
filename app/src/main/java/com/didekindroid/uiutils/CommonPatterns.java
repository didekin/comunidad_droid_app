package com.didekindroid.uiutils;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;

/**
 * User: pedro@didekin
 * Date: 19/05/15
 * Time: 11:09
 */
public enum CommonPatterns {


    SELECT("\\bselect\\b"),
    LINE_BREAK("\n"),
    ;

    public final Pattern pattern;
    public final String literal;

    CommonPatterns(String patternString)
    {
        literal = patternString;
        pattern = compile(literal, UNICODE_CASE | CASE_INSENSITIVE);
    }
}
