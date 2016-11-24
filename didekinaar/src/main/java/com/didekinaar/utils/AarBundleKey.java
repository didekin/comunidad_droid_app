package com.didekinaar.utils;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum AarBundleKey {

    IS_MENU_IN_FRAGMENT_FLAG,
    ;

    private static final String intentPackage =  "com.didekinaar.comunidad.ComuBundleKey.";

    public final String key;

    AarBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
