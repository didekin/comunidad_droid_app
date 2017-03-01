package com.didekindroid.incidencia.utils;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IncidBundleKey {

    INCID_ACTIVITY_VIEW_ID,
    INCID_IMPORTANCIA_OBJECT,
    INCIDENCIA_LIST_INDEX,
    INCIDENCIA_OBJECT,
    INCID_RESOLUCION_FLAG,
    INCID_RESOLUCION_OBJECT,
    ;

    private static final String intentPackage =  "com.didekindroid.incidencia.utils.IncidBundleKey.";

    public final String key;

    IncidBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
