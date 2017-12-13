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
    INCID_RESOLUCION_BUNDLE,
    INCIDENCIA_ID_LIST_SELECTED,
    INCIDENCIAS_CLOSED_LIST_FLAG,
    INCIDENCIA_OBJECT,
    INCID_RESOLUCION_OBJECT,;

    private static final String intentPackage = "com.didekindroid.incidencia.utils.IncidBundleKey.";

    public final String key;

    IncidBundleKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
