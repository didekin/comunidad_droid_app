package com.didekindroid.comunidad;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum ComuBundleKey {

    COMUNIDAD_LIST,
    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    ;

    private static final String intentPackage =  "ComuBundleKey.";

    public final String key;

    ComuBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
