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
    INCIDENCIA_LIST_INDEX,
    INCID_USERCOMU_LIST_OBJECT,
    INCIDENCIA_ROL,
    USERCOMU_LIST_OBJECT,
    ;

    private static final String intentPackage =  "com.didekindroid.common.utils.AppKeysForBundle.";

    public final String extra;

    AppKeysForBundle()
    {
        extra = intentPackage.concat(this.name());
    }
}
