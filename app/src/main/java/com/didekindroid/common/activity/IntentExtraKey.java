package com.didekindroid.common.activity;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IntentExtraKey {

    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    INCID_IMPORTANCIA_OBJECT,
    INCIDENCIA_LIST_ID,
    INCIDENCIA_LIST_INDEX,
    INCID_RESOLUCION_FLAG,
    INCID_RESOLUCION_OBJECT,
    USERCOMU_LIST_OBJECT,
    ;

    private static final String intentPackage =  "com.didekindroid.common.activity.IntentExtraKey.";

    public final String extra;

    IntentExtraKey()
    {
        extra = intentPackage.concat(this.name());
    }
}
