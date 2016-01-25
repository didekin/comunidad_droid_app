package com.didekindroid.common.utils;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum AppKeysForBundle {

    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    INCIDENCIA_LIST_ID,
    INCIDENCIA_USER_OBJECT,
    INCIDENCIA_LIST_INDEX,
    USERCOMU_LIST_OBJECT,
    ;

    private static final String intentPackage =  "com.didekindroid.common.utils.AppKeysForBundle.";

    public final String extra;

    AppKeysForBundle()
    {
        extra = intentPackage.concat(this.name());
    }
}
