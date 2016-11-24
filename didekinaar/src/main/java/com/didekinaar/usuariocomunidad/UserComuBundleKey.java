package com.didekinaar.usuariocomunidad;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum UserComuBundleKey {

    USERCOMU_LIST_OBJECT,
    ;

    private static final String intentPackage =  "com.didekinaar.usuariocomunidad.UserComuBundleKey.";

    public final String key;

    UserComuBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
