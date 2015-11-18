package com.didekin.common.domain;

import java.util.regex.Pattern;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 19:24
 */
public interface DataPatternsIf {

    boolean isPatternOk(String fieldToCheck);

    Pattern getPattern();

    String getRegexp();
}
