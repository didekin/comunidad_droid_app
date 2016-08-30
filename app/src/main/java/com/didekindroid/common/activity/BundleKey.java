package com.didekindroid.common.activity;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum BundleKey {

    COMUNIDAD_LIST,
    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    INCID_ACTIVITY_VIEW_ID,
    INCID_IMPORTANCIA_OBJECT,
    INCIDENCIA_LIST_INDEX,
    INCIDENCIA_OBJECT,
    INCID_RESOLUCION_FLAG,
    INCID_RESOLUCION_OBJECT,
    IS_MENU_IN_FRAGMENT_FLAG,
    USERCOMU_LIST_OBJECT,
    ;

    private static final String intentPackage =  "com.didekindroid.common.activity.BundleKey.";

    public final String key;

    BundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
