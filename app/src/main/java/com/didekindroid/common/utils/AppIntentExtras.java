package com.didekindroid.common.utils;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum AppIntentExtras {

    USERCOMU_LIST_OBJECT,
    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    ;

    private static final String intentPackage =  "com.didekindroid.common.utils.AppIntentExtras.";

    public final String extra;

    AppIntentExtras()
    {
        extra = intentPackage.concat(this.name());
    }
}
