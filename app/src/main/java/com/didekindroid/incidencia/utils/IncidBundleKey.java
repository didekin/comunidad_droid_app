package com.didekindroid.incidencia.utils;

import com.didekindroid.util.BundleKey;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum IncidBundleKey implements BundleKey {

    AMBITO_INCIDENCIA_POSITION,
    INCID_ACTIVITY_VIEW_ID,
    INCID_IMPORTANCIA_NUMBER,
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

    @Override
    public String getKey()
    {
        return key;
    }
}
